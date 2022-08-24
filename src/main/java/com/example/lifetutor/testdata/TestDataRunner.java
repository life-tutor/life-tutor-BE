package com.example.lifetutor.testdata;

import com.example.lifetutor.post.repository.PostRepository;
import com.example.lifetutor.room.model.Enter;
import com.example.lifetutor.room.model.Room;
import com.example.lifetutor.room.repository.RoomRepository;
import com.example.lifetutor.user.model.Role;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.user.repositroy.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RoomRepository roomRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = new User("t@t.t","king", passwordEncoder.encode("1234"), Role.SEEKER,false);
        User user1 = new User("a@a.a","king1", passwordEncoder.encode("1234"), Role.SEEKER,false);
        userRepository.save(user);
        userRepository.save(user1);

    }
}