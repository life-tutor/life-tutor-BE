package com.example.lifetutor.integration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.comment.repository.CommentRepository;
import com.example.lifetutor.post.dto.request.PostRequestDto;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.repository.PostRepository;
import com.example.lifetutor.user.model.Role;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.user.repositroy.UserRepository;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CommentIntegrationTest {
    private static long postingId = 0;
    private long commentId = 0;
    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    TestRestTemplate testRestTemplate;

    @BeforeAll
    private static void setDB(
            @Autowired UserRepository userRepository,
            @Autowired PostRepository postRepository,
            @Autowired PasswordEncoder passwordEncoder
            ){
        User user1 = new User("username","nickname", passwordEncoder.encode("1234"), Role.SEEKER,false);
        User user2 = new User("test","tester", passwordEncoder.encode("5678"), Role.SEEKER,false);
        User user3 = new User("test2","tester2", passwordEncoder.encode("0000"), Role.SEEKER,false);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        List<String> hashtag = new ArrayList<>();
        PostRequestDto postRequestDto = new PostRequestDto(
                user1,"title","content",hashtag
        );
        Post post = new Post(postRequestDto);
        postRepository.save(post);
        postingId = post.getId();
    }

    @BeforeEach
    public void getCommentId(
            @Autowired CommentRepository commentRepository
    ){
        List<Comment> comments = commentRepository.findAll();
        for(Comment comment : comments){
            commentId = comment.getId();
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
    @DisplayName("?????? ??????")
    class Delete{

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
                                "/api/board/"+postId+"/comment/"+commentId,
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertEquals("???????????? ?????? ??? ????????????.",response.getBody());
            }

            @Test
            @DisplayName("????????? ??????")
            void test2(){
                //given
                HttpHeaders headerToken = headerToken("test");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postingId+"/comment/"+commentId,
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
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
                                "/api/board/"+postingId+"/comment/"+commentId,
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
    @DisplayName("?????? ??????")
    class Modify{

        @Nested
        @DisplayName("??????")
        class Fail{

            @Test
            @DisplayName("????????? ??????")
            void test1(){
                //given
                long postId = 999;
                CommentRequest request = CommentRequest.builder()
                        .content("content").build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postId+"/comment/"+commentId,
                                HttpMethod.PUT,
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
                long commId = 999;

                CommentRequest request = CommentRequest.builder()
                        .content("content").build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postingId+"/comment/"+commId,
                                HttpMethod.PUT,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertEquals("????????? ?????? ??? ????????????.",response.getBody());
            }

            @Test
            @DisplayName("null")
            void test3(){
                //given
                CommentRequest request = CommentRequest.builder()
                        .content(null).build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postingId+"/comment/"+commentId,
                                HttpMethod.PUT,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("?????? ??????????????????.",response.getBody());
            }

            @Test
            @DisplayName("blank")
            void test4(){
                //given
                CommentRequest request = CommentRequest.builder()
                        .content("    ").build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postingId+"/comment/"+commentId,
                                HttpMethod.PUT,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("?????? ??????????????????.",response.getBody());
            }

            @Test
            @DisplayName("????????? ??????")
            void test5(){
                //given
                CommentRequest request = CommentRequest.builder()
                        .content("content").build();

                HttpHeaders headerToken = headerToken("test");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postingId+"/comment/"+commentId,
                                HttpMethod.PUT,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
                CommentRequest request = CommentRequest.builder()
                        .content("content").build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postingId+"/comment/"+commentId,
                                HttpMethod.PUT,
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
    @DisplayName("?????? ?????? ??????")
    class UnLike{

        @Nested
        @DisplayName("??????")
        class Fail{

            @Test
            @DisplayName("?????? ??????")
            void test1(){
                //given
                long commId = 999;

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/comment/"+commId+"/likes",
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertEquals("????????? ?????? ??? ????????????.",response.getBody());
            }

            @Test
            @DisplayName("?????? ??????")
            void test2(){
                //given
                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/comment/"+commentId+"/likes",
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
                                "/api/comment/"+commentId+"/likes",
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
    @DisplayName("?????? ??????")
    class Like{

        @Nested
        @DisplayName("??????")
        class Fail{

            @Test
            @DisplayName("?????? ??????")
            void test1(){
                //given
                long commId = 999;

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/comment/"+commId+"/likes",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertEquals("????????? ?????? ??? ????????????.",response.getBody());
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
                                "/api/comment/"+commentId+"/likes",
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
                                "/api/comment/"+commentId+"/likes",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertEquals("?????? ??????",response.getBody());
            }
        }
    }

    @Nested
    @DisplayName("?????? ??????")
    class Write{

        @Nested
        @DisplayName("??????")
        class Fail{

            @Test
            @DisplayName("????????? ??????")
            void test1(){
                //given
                long postId = 999;

                CommentRequest request = CommentRequest.builder()
                        .content("content").build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postId+"/comment",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertEquals("???????????? ?????? ??? ????????????.",response.getBody());
            }

            @Test
            @DisplayName("null")
            void test2(){
                //given
                CommentRequest request = CommentRequest.builder()
                        .content(null).build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postingId+"/comment",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("?????? ??????????????????.",response.getBody());
            }

            @Test
            @DisplayName("blank")
            void test3(){
                //given
                CommentRequest request = CommentRequest.builder()
                        .content("    ").build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postingId+"/comment",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("?????? ??????????????????.",response.getBody());
            }
        }

        @Nested
        @DisplayName("??????")
        class Success{

            @Test
            @DisplayName("?????? ??????")
            void test(){
                //given
                CommentRequest request = CommentRequest.builder()
                        .content("content").build();

                HttpHeaders headerToken = headerToken("username");
                HttpEntity<?> requestEntity = new HttpEntity<>(request, headerToken);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postingId+"/comment",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertEquals("?????? ??????",response.getBody());
            }
        }
    }

    @Getter
    @Builder
    static class CommentRequest{
        private String content;
    }
}
