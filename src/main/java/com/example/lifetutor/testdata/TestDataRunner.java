//package com.example.lifetutor.testdata;
//
//import com.example.lifetutor.post.model.Post;
//import com.example.lifetutor.post.repository.PostRepository;
//import com.example.lifetutor.user.model.Role;
//import com.example.lifetutor.user.model.User;
//import com.example.lifetutor.user.repositroy.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class TestDataRunner implements ApplicationRunner {
//
//    private final UserRepository userRepository;
//    private final PostRepository postRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        User user = new User("kingdoor@naver.com","king", passwordEncoder.encode("password"), Role.SEEKER);
//
//        userRepository.save(user);
//
//        Post post = new Post(1L,user,null,"ddd","dfdwf",null);
//        Post post2 = new Post(2L,user,null,"dddff","dwfvvsa",null);
//        postRepository.save(post);
//        postRepository.save(post2);
//    }
//}