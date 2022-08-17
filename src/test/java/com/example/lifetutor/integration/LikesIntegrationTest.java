package com.example.lifetutor.integration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LikesIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

//    @BeforeAll
//    private static void setDB(
//            @Autowired UserRepository userRepository,
//            @Autowired PostRepository postRepository,
//            @Autowired PasswordEncoder passwordEncoder
//    ){
//        User user1 = new User("username","nickname", passwordEncoder.encode("1234"), Role.SEEKER);
//        User user2 = new User("test","tester", passwordEncoder.encode("5678"), Role.SEEKER);
//        userRepository.save(user1);
//        userRepository.save(user2);
//        Post post = new Post(user1,"title","content");
//        postRepository.save(post);
//    }

    public HttpEntity<?> getHeader(String username){
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
    @DisplayName("게시글 공감 취소")
    class UnLike{

        @Nested
        @DisplayName("실패")
        class Fail{

            @Test
            @DisplayName("게시글 없음")
            void test1(){
                //given
                long postingId = 999;

                HttpEntity<?> requestEntity = getHeader("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postingId+"/likes",
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("게시글을 찾을 수 없습니다.",response.getBody());
            }

            @Test
            @DisplayName("공감 없음")
            void test2(){
                //given
                long postingId = 1;

                HttpEntity<?> requestEntity = getHeader("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postingId+"/likes",
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("공감한적 없습니다.",response.getBody());
            }
        }

        @Nested
        @DisplayName("성공")
        class Success{

            @Test
            @DisplayName("취소 정상")
            void test(){
                //given
                long postingId = 1;

                HttpEntity<?> requestEntity = getHeader("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postingId+"/likes",
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("공감 취소",response.getBody());
            }
        }
    }

    @Nested
    @DisplayName("게시글 공감")
    class Like{

        @Nested
        @DisplayName("실패")
        class Fail{

            @Test
            @DisplayName("게시글 없음")
            void test1(){
                //given
                long postingId = 999;

                HttpEntity<?> requestEntity = getHeader("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postingId+"/likes",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("게시글을 찾을 수 없습니다.",response.getBody());
            }

            @Test
            @DisplayName("이미 공감함")
            void test2(){
                //given
                long postingId = 1;

                HttpEntity<?> requestEntity = getHeader("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postingId+"/likes",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("이미 공감하셨습니다.",response.getBody());
            }
        }

        @Nested
        @DisplayName("성공")
        class Success{

            @Test
            @DisplayName("공감 정상")
            void test(){
                //given
                long postingId = 1;

                HttpEntity<?> requestEntity = getHeader("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postingId+"/likes",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertEquals("공감 성공",response.getBody());
            }
        }
    }
}
