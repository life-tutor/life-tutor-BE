package com.example.lifetutor.integration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RoomIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    public HttpEntity<?> getHeader(String username, RoomRequest request){
        Algorithm ALGORITHM = Algorithm.HMAC256("jwt_secret_!@#$%");
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

        return new HttpEntity<>(request,requestHeaders);
    }
    public HttpEntity<?> getHeader2(String username){
        Algorithm ALGORITHM = Algorithm.HMAC256("jwt_secret_!@#$%");
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

        return new HttpEntity<>(requestHeaders);
    }

    @Nested
    @DisplayName("채팅방 퇴장")
    class Exit{

        @Nested
        @DisplayName("실패")
        class Fail{

            @Test
            @DisplayName("room notFound")
            void test(){
                //given
                long roomId = 5;
                HttpEntity<?> requestEntity = getHeader2("test");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/chat/room/"+roomId+"/exit",
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
                long roomId = 5;
                HttpEntity<?> requestEntity = getHeader2("test");
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
                long roomId = 5;
                HttpEntity<?> requestEntity = getHeader2("username");
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
            @DisplayName("guest2 입장")
            void test(){
                //given
                long roomId = 5;
                HttpEntity<?> requestEntity = getHeader2("test2");
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
                long roomId = 5;
                HttpEntity<?> requestEntity = getHeader2("username");
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
                long roomId = 5;
                HttpEntity<?> requestEntity = getHeader2("test");
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
        }
    }

    @Nested @Disabled
    @DisplayName("채팅방 검색")
    class Search{}

    @Nested @Disabled
    @DisplayName("채팅방 조회")
    class List{}

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

                HttpEntity<?> requestEntity = getHeader("username",request);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/chat/room",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            }

            @Test
            @DisplayName("제목 blank")
            void test2(){
                //given
                RoomRequest request = RoomRequest.builder()
                        .title("   ").hashtag(new ArrayList<>()).build();

                HttpEntity<?> requestEntity = getHeader("username",request);
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
        }

        @Nested
        @DisplayName("성공")
        class Success{

            @Test
            @DisplayName("작성 정상")
            void test(){
                //given
                RoomRequest request = RoomRequest.builder()
                        .title("title").hashtag(new ArrayList<>()).build();

                HttpEntity<?> requestEntity = getHeader("username",request);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/chat/room",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                System.out.println("room_id: "+response.getBody());
            }
        }
    }

    @Builder
    @Getter
    public static class RoomRequest {
        private String title;
        private java.util.List<String> hashtag;
    }
}
