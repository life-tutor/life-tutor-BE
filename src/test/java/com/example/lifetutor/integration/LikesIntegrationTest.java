package com.example.lifetutor.integration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LikesIntegrationTest {
    private long postingId = 0;
    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    TestRestTemplate testRestTemplate;

    @BeforeEach
    public void getId(
            @Autowired PostRepository postRepository
    ){
        List<Post> posts = postRepository.findAll();
        for(Post post : posts){
            postingId = post.getId();
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
    @DisplayName("????????? ?????? ??????")
    class UnLike{

        @Nested
        @DisplayName("??????")
        class Fail{

            @Test
            @DisplayName("????????? ??????")
            void test1(){
                //given
                long postId = 999;

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postId+"/likes",
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertEquals("???????????? ?????? ??? ????????????.",response.getBody());
            }

            @Test
            @DisplayName("?????? ??????")
            void test2(){
                //given
                HttpHeaders headerToken = headerToken("test");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postingId+"/likes",
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertEquals("???????????? ????????????.",response.getBody());
            }
        }

        @Nested
        @DisplayName("??????")
        class Success{

            @Test
            @DisplayName("?????? ??????")
            void test(){
                //given
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
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
                assertEquals("?????? ??????",response.getBody());
            }
        }
    }

    @Nested
    @DisplayName("????????? ??????")
    class Like{

        @Nested
        @DisplayName("??????")
        class Fail{

            @Test
            @DisplayName("????????? ??????")
            void test1(){
                //given
                long postId = 999;

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postId+"/likes",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertEquals("???????????? ?????? ??? ????????????.",response.getBody());
            }

            @Test
            @DisplayName("?????? ?????????")
            void test2(){
                //given
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postingId+"/likes",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("?????? ?????????????????????.",response.getBody());
            }
        }

        @Nested
        @DisplayName("??????")
        class Success{

            @Test
            @DisplayName("?????? ??????")
            void test(){
                //given
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postingId+"/likes",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertEquals("?????? ??????",response.getBody());
            }
        }
    }
}
