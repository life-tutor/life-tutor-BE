package com.example.lifetutor.user.controller;

import com.example.lifetutor.config.security.UserDetailsImpl;
import com.example.lifetutor.user.dto.request.LeaveUserRequestDto;
import com.example.lifetutor.user.dto.request.SignupRequestDto;
import com.example.lifetutor.user.dto.request.UpdateMyInfoRequestDto;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequestDto signupRequestDto) {
        return userService.registerUser(signupRequestDto);
    }


    //내 정보 보기
    @GetMapping("user/info")
    public ResponseEntity<?> showMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return userService.showMyInfo(user);
    }

    @GetMapping("mypage/postings")
    public void showMyPost(@RequestParam int page, @RequestParam int size, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
    }

    //이메일 중복 체크
    @GetMapping("users/{email}")
    public void checkEmail(@PathVariable("email") String username) {
       userService.checkEmail(username);
    }

    //마이페이지 정보 수정
    @PutMapping("mypage/user")
    public ResponseEntity<?> updateMyInfo(@RequestBody UpdateMyInfoRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return userService.updateMyInfo(requestDto,user);
    }

    //회원 탈퇴
    @DeleteMapping("mypage/user")
    public ResponseEntity<?> leaveUser(@RequestBody LeaveUserRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return userService.leaveUser(requestDto,user);
    }
}