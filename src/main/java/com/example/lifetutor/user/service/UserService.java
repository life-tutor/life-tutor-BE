package com.example.lifetutor.user.service;

import com.example.lifetutor.config.security.UserDetailsServiceImpl;
import com.example.lifetutor.user.dto.request.LeaveUserRequestDto;
import com.example.lifetutor.user.dto.request.SignupRequestDto;
import com.example.lifetutor.user.dto.request.UpdateMyInfoRequestDto;
import com.example.lifetutor.user.dto.response.MyPageResponseDto;
import com.example.lifetutor.user.model.Role;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.user.repositroy.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserDetailsServiceImpl userDetailsService;

    //회원가입
    public ResponseEntity<?> registerUser(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String nickname = signupRequestDto.getNickname();
        String password = signupRequestDto.getPassword();
        String checkPassword = signupRequestDto.getCheckPassword();
        Role userType = signupRequestDto.getUser_type();


        Optional<User> nicknameFound = userRepository.findByNickname(nickname);
        if (nicknameFound.isPresent()) {
            return new ResponseEntity<>("이미 존재하는 닉네임 입니다.", HttpStatus.valueOf(400));
        }
        if (!password.equals(checkPassword)) {
            return new ResponseEntity<>("비밀번호를 확인해주세요.", HttpStatus.valueOf(400));
        }

        User user = new User(username, nickname, passwordEncoder.encode(password), userType);

        userRepository.save(user);

        return new ResponseEntity<>("가입 성공", HttpStatus.valueOf(200));
    }

    public ResponseEntity<?> showMyInfo(User user) {
        String username = user.getUsername();
        String nickname = user.getNickname();

        MyPageResponseDto myPageResponseDto = new MyPageResponseDto(username, nickname);

        return new ResponseEntity<>(myPageResponseDto, HttpStatus.valueOf(200));
    }

    public void checkEmail(String username) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("중복된 이메일 입니다.");
    }

    public void checkNickname(String nickname) {
        if (userRepository.existsByNickname(nickname))
            throw new IllegalArgumentException("중복된 닉네임 입니다.");
    }

    @Transactional
    public ResponseEntity<?> updateMyInfo(UpdateMyInfoRequestDto requestDto, User user) {

        if (!requestDto.getPassword().equals(requestDto.getCheckPassword()))
            return new ResponseEntity<>("비밀번호를 확인해주세요.", HttpStatus.valueOf(400));

        if(!passwordEncoder.matches(requestDto.getPassword(),user.getPassword()))
            return new ResponseEntity<>("비밀번호를 확인해주세요.", HttpStatus.valueOf(400));

        if(userRepository.existsByNickname(requestDto.getNickname()))
            return new ResponseEntity<>("닉네임이 중복됩니다.", HttpStatus.valueOf(400));

        user.updateMyInfo(requestDto);

        userRepository.save(user);

        return new ResponseEntity<>("변경이 완료 되었습니다.", HttpStatus.valueOf(200));
    }


    public ResponseEntity<?> leaveUser(LeaveUserRequestDto requestDto, User user) {
        if(!requestDto.getPassword().equals(requestDto.getCheckPassword()))
            return new ResponseEntity<>("비밀번호를 확인해주세요.", HttpStatus.valueOf(400));

        if(!passwordEncoder.matches(requestDto.getPassword(),user.getPassword()))
            return new ResponseEntity<>("비밀번호를 확인해주세요.", HttpStatus.valueOf(400));
        
        userRepository.delete(user);

        return new ResponseEntity<>("탈퇴가 완료 되었습니다.", HttpStatus.valueOf(200));
    }
}
