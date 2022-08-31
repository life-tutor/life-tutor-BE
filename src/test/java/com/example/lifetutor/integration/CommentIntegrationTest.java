package com.example.lifetutor.integration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
    }

    public HttpEntity<?> getHeader(String username, CommentRequest request){
        Algorithm ALGORITHM = Algorithm.HMAC256("IT_ING!@#!@#!@");
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
        Algorithm ALGORITHM = Algorithm.HMAC256("IT_ING!@#!@#!@");
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
    @DisplayName("댓글 삭제")
    class Delete{

        @Nested
        @DisplayName("실패")
        class Fail{

            @Test
            @DisplayName("게시글 없음")
            void test1(){
                //given
                long posingId = 999;
                long commentId = 3;

                HttpEntity<?> requestEntity = getHeader2("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+posingId+"/comment/"+commentId,
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("게시글을 찾을 수 없습니다.",response.getBody());
            }

            @Test
            @DisplayName("작성자 아님")
            void test2(){
                //given
                long posingId = 2;
                long commentId = 3;

                HttpEntity<?> requestEntity = getHeader2("test");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+posingId+"/comment/"+commentId,
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            }
        }

        @Nested
        @DisplayName("성공")
        class Success{

            @Test
            @DisplayName("삭제 정상")
            void test(){
                //given
                long posingId = 2;
                long commendId = 3;

                HttpEntity<?> requestEntity = getHeader2("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+posingId+"/comment/"+commendId,
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("삭제 성공",response.getBody());
            }
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class Modify{

        @Nested
        @DisplayName("실패")
        class Fail{

            @Test
            @DisplayName("게시글 없음")
            void test1(){
                //given
                long postingId = 999;
                long commentId = 3;

                CommentRequest request = CommentRequest.builder()
                        .content("content").build();

                HttpEntity<?> requestEntity = getHeader("username",request);
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
                assertEquals("게시글을 찾을 수 없습니다.",response.getBody());
            }

            @Test
            @DisplayName("댓글 없음")
            void test2(){
                //given
                long postingId = 2;
                long commentId = 999;

                CommentRequest request = CommentRequest.builder()
                        .content("content").build();

                HttpEntity<?> requestEntity = getHeader("username",request);
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
                assertEquals("댓글을 찾을 수 없습니다.",response.getBody());
            }

            @Test
            @DisplayName("null")
            void test3(){
                //given
                long postingId = 2;
                long commentId = 3;

                CommentRequest request = CommentRequest.builder()
                        .content(null).build();

                HttpEntity<?> requestEntity = getHeader("username",request);
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
                assertEquals("값을 입력해주세요.",response.getBody());
            }

            @Test
            @DisplayName("blank")
            void test4(){
                //given
                long postingId = 2;
                long commentId = 3;

                CommentRequest request = CommentRequest.builder()
                        .content("    ").build();

                HttpEntity<?> requestEntity = getHeader("username",request);
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
                assertEquals("값을 입력해주세요.",response.getBody());
            }

            @Test
            @DisplayName("작성자 아님")
            void test5(){
                //given
                long postingId = 2;
                long commentId = 3;

                CommentRequest request = CommentRequest.builder()
                        .content("content").build();

                HttpEntity<?> requestEntity = getHeader("test",request);
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
                assertEquals("작성자가 아닙니다.",response.getBody());
            }
        }

        @Nested
        @DisplayName("성공")
        class Success{

            @Test
            @DisplayName("수정 정상")
            void test(){
                //given
                long postingId = 2;
                long commendId = 3;

                CommentRequest request = CommentRequest.builder()
                        .content("content").build();

                HttpEntity<?> requestEntity = getHeader("username",request);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/board/"+postingId+"/comment/"+commendId,
                                HttpMethod.PUT,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("수정 성공",response.getBody());
            }
        }
    }

    @Nested
    @DisplayName("댓글 공감 취소")
    class UnLike{

        @Nested
        @DisplayName("실패")
        class Fail{

            @Test
            @DisplayName("댓글 없음")
            void test1(){
                //given
                long commentId = 999;

                HttpEntity<?> requestEntity = getHeader2("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/comment/"+commentId+"/likes",
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("댓글을 찾을 수 없습니다.",response.getBody());
            }

            @Test
            @DisplayName("공감 없음")
            void test2(){
                //given
                long commentId = 3;

                HttpEntity<?> requestEntity = getHeader2("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .exchange(
                                "/api/comment/"+commentId+"/likes",
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
                long commentId = 3;

                HttpEntity<?> requestEntity = getHeader2("username");
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
                assertEquals("공감 취소",response.getBody());
            }
        }
    }

    @Nested
    @DisplayName("댓글 공감")
    class Like{

        @Nested
        @DisplayName("실패")
        class Fail{

            @Test
            @DisplayName("댓글 없음")
            void test1(){
                //given
                long commentId = 999;

                HttpEntity<?> requestEntity = getHeader2("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/comment/"+commentId+"/likes",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("댓글을 찾을 수 없습니다.",response.getBody());
            }

            @Test
            @DisplayName("이미 공감함")
            void test2(){
                //given
                long commentId = 3;

                HttpEntity<?> requestEntity = getHeader2("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/comment/"+commentId+"/likes",
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
                long commentId = 3;

                HttpEntity<?> requestEntity = getHeader2("username");
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/comment/"+commentId+"/likes",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertEquals("공감 성공",response.getBody());
            }
        }
    }

    @Nested
    @DisplayName("댓글 작성")
    class Write{

        @Nested
        @DisplayName("실패")
        class Fail{

            @Test
            @DisplayName("게시글 없음")
            void test1(){
                //given
                long postingId = 999;

                CommentRequest request = CommentRequest.builder()
                        .content("content").build();

                HttpEntity<?> requestEntity = getHeader("username",request);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postingId+"/comment",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("게시글을 찾을 수 없습니다.",response.getBody());
            }

            @Test
            @DisplayName("null")
            void test2(){
                //given
                long postingId = 2;

                CommentRequest request = CommentRequest.builder()
                        .content(null).build();

                HttpEntity<?> requestEntity = getHeader("username",request);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postingId+"/comment",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("값을 입력해주세요.",response.getBody());
            }

            @Test
            @DisplayName("blank")
            void test3(){
                //given
                long postingId = 2;

                CommentRequest request = CommentRequest.builder()
                        .content("    ").build();

                HttpEntity<?> requestEntity = getHeader("username",request);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postingId+"/comment",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("값을 입력해주세요.",response.getBody());
            }
        }

        @Nested
        @DisplayName("성공")
        class Success{

            @Test
            @DisplayName("작성 정상")
            void test(){
                //given
                long postingId = 2;

                CommentRequest request = CommentRequest.builder()
                        .content("content").build();

                HttpEntity<?> requestEntity = getHeader("username",request);
                //when
                ResponseEntity<String> response = testRestTemplate
                        .postForEntity(
                                "/api/board/"+postingId+"/comment",
                                requestEntity,
                                String.class
                        );
                //then
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertEquals("작성 성공",response.getBody());
            }
        }
    }

    @Getter
    @Builder
    static class CommentRequest{
        String content;
    }
}
