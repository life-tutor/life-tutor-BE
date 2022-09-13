package com.example.lifetutor.integration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.lifetutor.hashtag.dto.response.HashtagDto;
import com.example.lifetutor.room.dto.response.RoomResponseDto;
import com.example.lifetutor.room.model.Room;
import com.example.lifetutor.room.repository.RoomRepository;
import com.example.lifetutor.user.model.Role;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("채팅방 퇴장")
    class Exit{

        @Nested
        @DisplayName("실패")
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
                assertEquals("방을 찾을 수 없습니다.",response.getBody());
            }
        }

        @Nested
        @DisplayName("성공")
        class Success{

            @Test
            @DisplayName("guest1 퇴장")
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
                assertEquals("퇴장",response.getBody());
            }

            @Test
            @DisplayName("host 퇴장")
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
                assertEquals("퇴장",response.getBody());
            }
        }
    }

    @Nested
    @DisplayName("채팅방 입장")
    class Enter{

        @Nested
        @DisplayName("실패")
        class Fail{

            @Test
            @DisplayName("room notFound")
            void test1(){
                long room = 999;
                //given
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/chat/room/"+room+"/enter",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertEquals("방을 찾을 수 없습니다.",response.getBody());
            }

            @Test
            @DisplayName("guest2 입장")
            void test2(){
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
                assertEquals("인원이 다 차서 입장이 불가합니다.",response.getBody());
            }
        }

        @Nested
        @DisplayName("성공")
        class Success{

            @Test
            @DisplayName("host 입장")
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
            @DisplayName("guest1 입장")
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
            @DisplayName("(비정상 종료 후) host 재입장")
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

    @Nested
    @DisplayName("채팅방 검색")
    class Search{

        @Nested
        @DisplayName("실패")
        class Fail{

            @Test
            @DisplayName("해시태그 1글자")
            void test1(){
                //given
                String keyword = "해";
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);

                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/hashtags/rooms?hashtag="+keyword,
                                HttpMethod.GET,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
                assertEquals("2자 ~ 6자까지 입력해주세요.",response.getBody());
            }

            @Test
            @DisplayName("해시태그 7글자")
            void test2(){
                //given
                String keyword = "해시태그해시태";
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);

                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/hashtags/rooms?hashtag="+keyword,
                                HttpMethod.GET,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
                assertEquals("2자 ~ 6자까지 입력해주세요.",response.getBody());
            }

            @Test
            @DisplayName("해시태그 NotFound")
            void test3(){
                //given
                String keyword = "테스트";
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);

                //when
                ResponseEntity<ArrayList<HashtagDto>> response = testRestTemplate
                        .exchange(
                                "/api/hashtags/rooms?hashtag=" + keyword,
                                HttpMethod.GET,
                                requestEntity,
                                new ParameterizedTypeReference<ArrayList<HashtagDto>>() {}
                        );
                //then
                ArrayList<HashtagDto> result = response.getBody();
                assertNotNull(result);
                assertEquals(0,result.size());
            }
        }

        @Nested
        @DisplayName("성공")
        class Success{

            @Test
            @DisplayName("해시태그 리스트 정상")
            void test1(){
                //given
                String keyword = "해시";
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);

                //when
                ResponseEntity<ArrayList<HashtagDto>> response = testRestTemplate
                        .exchange(
                                "/api/hashtags/rooms?hashtag=" + keyword,
                                HttpMethod.GET,
                                requestEntity,
                                new ParameterizedTypeReference<ArrayList<HashtagDto>>() {}
                        );
                //then
                assertEquals(HttpStatus.OK,response.getStatusCode());
                ArrayList<HashtagDto> result = response.getBody();
                assertNotNull(result);
                assertEquals("해시태그해시",result.get(0).getHashtag());
                assertEquals(1,result.get(0).getCount());
            }

            @Test
            @DisplayName("해시태그 검색 정상")
            void test2(){
                //given
                String hashtag = "해시태그해시";
                int page = 0;
                int size = 10;
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);

                //when
                ResponseEntity<RoomResponseDto> response = testRestTemplate
                        .exchange(
                                "/api/search/rooms?hashtag="+hashtag+"&page="+page+"&size="+size,
                                HttpMethod.GET,
                                requestEntity,
                                RoomResponseDto.class
                        );
                //then
                assertEquals(HttpStatus.OK,response.getStatusCode());
                RoomResponseDto result = response.getBody();
                assertNotNull(result);
                assertTrue(result.getIsLast());
                assertEquals(1,result.getContent().size());
                assertEquals(roomId,result.getContent().get(0).getRoomId());
                assertEquals("username",result.getContent().get(0).getUsername());
                assertEquals("nickname",result.getContent().get(0).getNickname());
                assertEquals(Role.SEEKER,result.getContent().get(0).getUser_type());
                assertEquals("title",result.getContent().get(0).getTitle());
                assertFalse(result.getContent().get(0).isIsfull());
                assertEquals("해시태그해시",result.getContent().get(0).getHashtag().get(0));
            }
        }
    }

    @Nested
    @DisplayName("채팅방 조회")
    class List{

        @Nested
        @DisplayName("성공")
        class Success{

            @Test
            @DisplayName("조회 정상")
            void test(){
                //given
                int page = 1;
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
                assertTrue(result.getIsLast());
                assertEquals(1,result.getContent().size());
                assertEquals(roomId,result.getContent().get(0).getRoomId());
                assertEquals("username",result.getContent().get(0).getUsername());
                assertEquals("nickname",result.getContent().get(0).getNickname());
                assertEquals(Role.SEEKER,result.getContent().get(0).getUser_type());
                assertEquals("title",result.getContent().get(0).getTitle());
                assertFalse(result.getContent().get(0).isIsfull());
                assertEquals("해시태그해시",result.getContent().get(0).getHashtag().get(0));
            }
        }
    }

    @Nested
    @DisplayName("채팅방 생성")
    class Create{

        @Nested
        @DisplayName("실패")
        class Fail{

            @Test
            @DisplayName("제목 null")
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
                assertEquals("제목을 입력해주세요.", response.getBody());
            }

            @Test
            @DisplayName("제목 blank")
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
                assertEquals("제목을 입력해주세요.",response.getBody());
            }

            @Test
            @DisplayName("해시태그 1글자")
            void test3(){
                //given
                ArrayList<String> hashtag = new ArrayList<>();
                hashtag.add("해");
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
                assertEquals("2자 ~ 6자까지 입력해주세요.",response.getBody());
            }

            @Test
            @DisplayName("해시태그 7글자")
            void test4(){
                //given
                ArrayList<String> hashtag = new ArrayList<>();
                hashtag.add("해시태그해시태");
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
                assertEquals("2자 ~ 6자까지 입력해주세요.",response.getBody());
            }
        }

        @Nested
        @DisplayName("성공")
        class Success{

            @Test
            @DisplayName("작성 정상")
            void test1(){
                //given
                ArrayList<String> hashtag = new ArrayList<>();
                hashtag.add("해시태그해시");
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
