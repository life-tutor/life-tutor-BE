package com.example.lifetutor.integration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.lifetutor.room.dto.response.RoomResponseDto;
import com.example.lifetutor.room.model.Room;
import com.example.lifetutor.room.repository.RoomRepository;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RoomIntegrationTest {
    private long roomId = 0;
    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    TestRestTemplate testRestTemplate;

    @BeforeEach
    public void getId(
            @Autowired RoomRepository roomRepository
    ){
        java.util.List<Room> rooms = roomRepository.findAll();
        for(Room room : rooms){
            roomId = room.getId();
        }
    }
    public HttpHeaders headerToken(String username){
        Algorithm ALGORITHM = Algorithm.HMAC256(secretKey);
        String token = JWT.create()
                .withIssuer("sparta")
                .withClaim("USER_NAME", username)
                .withClaim("EXPIRED_DATE", Instant.now().getEpochSecond() + 60*60)
                .sign(ALGORITHM);
        String authorizationHeader = "Bearer " + token;

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        requestHeaders.add("Authorization",authorizationHeader);
        return requestHeaders;
    }

    @Nested
    @DisplayName("????????? ??????")
    class Exit{

        @Nested
        @DisplayName("??????")
        class Fail{

            @Test @Disabled
            @DisplayName("room notFound")
            void test(){
                long room = 999;
                //given
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/chat/room/"+room+"/exit",
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertEquals("?????? ?????? ??? ????????????.",response.getBody());
            }
        }

        @Nested
        @DisplayName("??????")
        class Success{

            @Test
            @DisplayName("guest1 ??????")
            void test1(){
                //given
                HttpHeaders headerToken = headerToken("test");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/chat/room/"+roomId+"/exit",
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("??????",response.getBody());
            }

            @Test
            @DisplayName("host ??????")
            void test2(){
                //given
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/chat/room/"+roomId+"/exit",
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("??????",response.getBody());
            }
        }
    }

    @Nested
    @DisplayName("????????? ??????")
    class Enter{

        @Nested
        @DisplayName("??????")
        class Fail{

            @Test
            @DisplayName("guest2 ??????")
            void test(){
                //given
                HttpHeaders headerToken = headerToken("test2");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/chat/room/"+roomId+"/enter",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("????????? ??? ?????? ????????? ???????????????.",response.getBody());
            }
        }

        @Nested
        @DisplayName("??????")
        class Success{

            @Test
            @DisplayName("host ??????")
            void test1(){
                //given
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/chat/room/"+roomId+"/enter",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("nickname",response.getBody());
            }

            @Test
            @DisplayName("guest1 ??????")
            void test2(){
                //given
                HttpHeaders headerToken = headerToken("test");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/chat/room/"+roomId+"/enter",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("tester",response.getBody());
            }

            @Test
            @DisplayName("(????????? ?????? ???) host ?????????")
            void test3(){
                //given
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/chat/room/"+roomId+"/enter",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("nickname",response.getBody());
            }
        }
    }

    @Nested @Disabled
    @DisplayName("????????? ??????")
    class Search{}

    @Nested
    @DisplayName("????????? ??????")
    class List{

        @Nested
        @DisplayName("??????")
        class Fail{}

        @Nested
        @DisplayName("??????")
        class Success{

            @Test @Disabled
            @DisplayName("?????? ??????")
            void test0(){
                //given
                int page = 0;
                int size = 10;
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);

                //when
                ResponseEntity<RoomResponseDto> response = testRestTemplate
                        .exchange(
                                "/api/main/rooms?page="+page+"&size="+size,
                                HttpMethod.GET,
                                requestEntity,
                                RoomResponseDto.class
                        );
                //then
                assertEquals(HttpStatus.OK,response.getStatusCode());
                RoomResponseDto result = response.getBody();
                assertNotNull(result);
//                assertTrue(result.getIsLast());
//                assertEquals(1,result.getContent().size());
//                assertEquals(roomId,result.getContent().get(0).getRoomId());
//                assertEquals("username",result.getContent().get(0).getUsername());
//                assertEquals("nickname",result.getContent().get(0).getNickname());
//                assertEquals("SEEKER",result.getContent().get(0).getUser_type());
//                assertEquals("title",result.getContent().get(0).getTitle());
//                assertEquals(false,result.getContent().get(0).isIsfull());
//                assertEquals("??????????????????",result.getContent().get(0).getHashtag().get(0));
            }
        }
    }

    @Nested
    @DisplayName("????????? ??????")
    class Create{

        @Nested
        @DisplayName("??????")
        class Fail{

            @Test
            @DisplayName("?????? null")
            void test1(){
                //given
                RoomRequest request = RoomRequest.builder()
                        .title(null).hashtag(new ArrayList<>()).build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/chat/room",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("????????? ??????????????????.", response.getBody());
            }

            @Test
            @DisplayName("?????? blank")
            void test2(){
                //given
                RoomRequest request = RoomRequest.builder()
                        .title("   ").hashtag(new ArrayList<>()).build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/chat/room",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("????????? ??????????????????.",response.getBody());
            }

            @Test
            @DisplayName("???????????? 1??????")
            void test3(){
                //given
                ArrayList<String> hashtag = new ArrayList<>();
                hashtag.add("???");
                RoomRequest request = RoomRequest.builder()
                        .title("title").hashtag(hashtag).build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/chat/room",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("2??? ~ 6????????? ??????????????????.",response.getBody());
            }

            @Test
            @DisplayName("???????????? 7??????")
            void test4(){
                //given
                ArrayList<String> hashtag = new ArrayList<>();
                hashtag.add("?????????????????????");
                RoomRequest request = RoomRequest.builder()
                        .title("title").hashtag(hashtag).build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/chat/room",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("2??? ~ 6????????? ??????????????????.",response.getBody());
            }
        }

        @Nested
        @DisplayName("??????")
        class Success{

            @Test
            @DisplayName("?????? ??????")
            void test1(){
                //given
                ArrayList<String> hashtag = new ArrayList<>();
                hashtag.add("??????????????????");
                RoomRequest request = RoomRequest.builder()
                        .title("title").hashtag(hashtag).build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<Long> response = testRestTemplate
                        .postForEntity(
                                "/api/chat/room",
                                requestEntity,
                                Long.class
                        );
                //then
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
            }
        }
    }

    @Builder
    @Getter
    public static class RoomRequest {
        private String title;
        private ArrayList<String> hashtag;
    }
}
