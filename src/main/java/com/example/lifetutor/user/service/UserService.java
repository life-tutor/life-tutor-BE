package com.example.lifetutor.user.service;

import com.example.lifetutor.config.security.UserDetailsServiceImpl;
import com.example.lifetutor.hashtag.model.Hashtag;
import com.example.lifetutor.hashtag.model.PostHashtag;
import com.example.lifetutor.hashtag.repository.HashtagRepository;
import com.example.lifetutor.hashtag.repository.PostHashtagRepository;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.repository.PostRepository;
import com.example.lifetutor.user.dto.request.LeaveUserRequestDto;
import com.example.lifetutor.user.dto.request.SignupRequestDto;
import com.example.lifetutor.user.dto.request.UpdateMyInfoRequestDto;
import com.example.lifetutor.user.dto.request.UpdateMyPasswordRequestDto;
import com.example.lifetutor.user.dto.response.ContentResponseDto;
import com.example.lifetutor.user.dto.response.MyPageResponseDto;
import com.example.lifetutor.user.dto.response.ShowMyPostsResponseDto;
import com.example.lifetutor.user.model.Role;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.user.repositroy.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
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

        User user = new User(username, nickname, passwordEncoder.encode(password), userType,false);

        userRepository.save(user);

        return new ResponseEntity<>("가입 성공", HttpStatus.valueOf(200));
    }

    public ResponseEntity<?> showMyInfo(User user) {
        String username = user.getUsername();
        String nickname = user.getNickname();
        Role user_type = user.getUser_type();
        boolean isKakao = user.isKakao();

        MyPageResponseDto myPageResponseDto = new MyPageResponseDto(username, nickname,user_type,isKakao);

        return new ResponseEntity<>(myPageResponseDto, HttpStatus.valueOf(200));
    }

    public ResponseEntity<ShowMyPostsResponseDto> showMyPosts(int page, int size, User user) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "date");

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> posts = postRepository.findAllByUser(pageable, user);

        List<Post> postList = posts.getContent();

        List<ContentResponseDto> contentResponseDtos = new ArrayList<>();
        for (Post post : postList) {
            Long postingId = post.getId();
            String nickname = post.getUser().getNickname();
            String title = post.getTitle();
            LocalDateTime date = post.getDate();
            String content = post.getPosting_content();
            List<PostHashtag> hashtag = postHashtagRepository.findAllByPostId(post.getId());
            int comment_count = post.getComments().size();
            int like_count = post.getLikes().size();

            List<String> hashtags = new ArrayList<>();

            for (PostHashtag postHashtag : hashtag) {
                Hashtag hashtag1 = hashtagRepository.findById(postHashtag.getHashtag().getId()).get();
                hashtags.add(hashtag1.getHashtag());
            }

            ContentResponseDto contentResponseDto = new ContentResponseDto(postingId, nickname, title, date, content, hashtags, comment_count, like_count);
            contentResponseDtos.add(contentResponseDto);
        }
        ShowMyPostsResponseDto showMyPostsResponseDto = new ShowMyPostsResponseDto(contentResponseDtos, posts.isLast());
        return new ResponseEntity<>(showMyPostsResponseDto, HttpStatus.valueOf(200));
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

        if(userRepository.existsByNickname(requestDto.getNickname()))
            return new ResponseEntity<>("중복된 닉네임 입니다.", HttpStatus.valueOf(400));

        user.updateMyInfo(requestDto);

        userRepository.save(user);

        return new ResponseEntity<>("변경이 완료 되었습니다.", HttpStatus.valueOf(200));
    }

    @Transactional
    public ResponseEntity<?> updateMyPassword(UpdateMyPasswordRequestDto requestDto, User user) {
        if(!passwordEncoder.matches(requestDto.getPassword(),user.getPassword()))
            return new ResponseEntity<>("비밀번호를 확인해주세요.", HttpStatus.valueOf(400));

        if(!requestDto.getChangePassword().equals(requestDto.getConfirmChangePassword()))
            return new ResponseEntity<>("변경하는 비밀번호가 일치하지 않습니다.", HttpStatus.valueOf(400));

        if(passwordEncoder.matches(requestDto.getChangePassword(), user.getPassword()))
            return new ResponseEntity<>("동일한 비밀번호로 변경이 불가능 합니다.", HttpStatus.valueOf(400));

        user.updateMyPassword(passwordEncoder.encode(requestDto.getChangePassword()));

        userRepository.save(user);

        return new ResponseEntity<>("변경이 완료 되었습니다.", HttpStatus.valueOf(200));
    }

    public ResponseEntity<?> leaveUser(LeaveUserRequestDto requestDto, User user) {
        if(!requestDto.getPassword().equals(requestDto.getCheckPassword()))
            return new ResponseEntity<>("변경하는 비밀번호가 일치하지 않습니다.", HttpStatus.valueOf(400));

        if(!passwordEncoder.matches(requestDto.getPassword(),user.getPassword()))
            return new ResponseEntity<>("비밀번호를 확인해주세요.", HttpStatus.valueOf(400));
        
        userRepository.delete(user);

        return new ResponseEntity<>("탈퇴가 완료 되었습니다.", HttpStatus.valueOf(200));
    }
}
