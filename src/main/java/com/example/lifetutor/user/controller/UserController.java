package com.example.lifetutor.user.controller;

import com.example.lifetutor.config.security.UserDetailsImpl;
import com.example.lifetutor.user.dto.request.LeaveUserRequestDto;
import com.example.lifetutor.user.dto.request.SignupRequestDto;
import com.example.lifetutor.user.dto.request.UpdateMyInfoRequestDto;
import com.example.lifetutor.user.dto.request.UpdateMyPasswordRequestDto;
import com.example.lifetutor.user.dto.response.ShowMyCommentInPostResponseDto;
import com.example.lifetutor.user.dto.response.ShowMyPostsResponseDto;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("signup")
    public ResponseEntity<?> registerUser(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        return userService.registerUser(signupRequestDto);
    }

    //내 정보 보기
    @GetMapping("user/info")
    public ResponseEntity<?> showMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return userService.showMyInfo(user);
    }

    //내가 쓴 글 보기
    @GetMapping("mypage/postings")
    public ResponseEntity<ShowMyPostsResponseDto> showMyPost(@RequestParam int page, @RequestParam int size, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return userService.showMyPosts(page,size,user);
    }
    //내가 댓글 단 글 보기
    @GetMapping("mypage/comments/postings")
    public ResponseEntity<ShowMyCommentInPostResponseDto> showMyCommentInPost(@RequestParam int page, @RequestParam int size, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return userService.showMyCommentInPost(page,size,user);
    }

    //이메일 중복 체크
    @GetMapping("users/email/{email}")
    public void checkEmail(@PathVariable("email") String username) {
       userService.checkEmail(username);
    }

    //닉네임 중복 체크
    @GetMapping("users/nickname/{nickname}")
    public void checkNickname(@PathVariable("nickname") String nickname) {
        userService.checkNickname(nickname);
    }

    //마이페이지 정보 수정
    @PutMapping("mypage/user/info")
    public ResponseEntity<?> updateMyInfo(@RequestBody UpdateMyInfoRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return userService.updateMyInfo(requestDto,user);
    }

    //패스워드 체인지
    @PutMapping("mypage/user/password")
    public ResponseEntity<?> updateMyInfo(@RequestBody UpdateMyPasswordRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return userService.updateMyPassword(requestDto,user);
    }

    //리프레시 토큰
    @PutMapping("/refreshToken")
    public ResponseEntity<?> reIssueRefreshToken(HttpServletRequest request) {
        return userService.reIssueRefreshToken(request);
    }
}
