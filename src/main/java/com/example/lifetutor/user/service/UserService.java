package com.example.lifetutor.user.service;

import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.comment.repository.CommentRepository;
import com.example.lifetutor.config.security.jwt.HeaderTokenExtractor;
import com.example.lifetutor.config.security.jwt.JwtDecoder;
import com.example.lifetutor.config.security.jwt.JwtTokenUtils;
import com.example.lifetutor.hashtag.model.Hashtag;
import com.example.lifetutor.hashtag.model.PostHashtag;
import com.example.lifetutor.hashtag.repository.HashtagRepository;
import com.example.lifetutor.hashtag.repository.PostHashtagRepository;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.repository.PostRepository;
import com.example.lifetutor.user.dto.request.SignupRequestDto;
import com.example.lifetutor.user.dto.request.UpdateMyInfoRequestDto;
import com.example.lifetutor.user.dto.request.UpdateMyPasswordRequestDto;
import com.example.lifetutor.user.dto.response.ContentResponseDto;
import com.example.lifetutor.user.dto.response.MyPageResponseDto;
import com.example.lifetutor.user.dto.response.ShowMyPostsResponseDto;
import com.example.lifetutor.user.model.Auth;
import com.example.lifetutor.user.model.Role;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.user.repositroy.AuthRepository;
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

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final CommentRepository commentRepository;
    private final AuthRepository authRepository;
    private final JwtDecoder jwtDecoder;
    private final HeaderTokenExtractor extractor;
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

    public ResponseEntity<ShowMyPostsResponseDto> showMyCommentInPost(int page, int size, User user) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "date");

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> comments = commentRepository.findAllByUser(pageable,user);

        List<Comment> commentList = comments.getContent();

        List<ContentResponseDto> contentResponseDtos = new ArrayList<>();
        for(int i = 0 ; i < commentList.size(); i++) {
            Long postingId = commentList.get(i).getPost().getId();
            String nickname = commentList.get(i).getPost().getUser().getNickname();
            String title = commentList.get(i).getPost().getTitle();
            LocalDateTime date = commentList.get(i).getDate();
            String content = commentList.get(i).getPost().getPosting_content();
            List<PostHashtag> hashtag = postHashtagRepository.findAllByPostId(commentList.get(i).getPost().getId());
            int comment_count = commentList.get(i).getPost().getComments().size();
            int like_count = commentList.get(i).getLikes().size();

            List<String> hashtags = new ArrayList<>();

            for (PostHashtag postHashtag : hashtag) {
                Hashtag hashtag1 = hashtagRepository.findById(postHashtag.getHashtag().getId()).get();
                hashtags.add(hashtag1.getHashtag());
            }

            ContentResponseDto contentResponseDto = new ContentResponseDto(postingId, nickname, title, date, content, hashtags, comment_count, like_count);
            contentResponseDtos.add(contentResponseDto);
        }
        ShowMyPostsResponseDto showMyPostsResponseDto = new ShowMyPostsResponseDto(contentResponseDtos,comments.isLast());
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
        if(requestDto.getNickname() == null || requestDto.getNickname().equals(""))
            return new ResponseEntity<>("닉네임을 입력해주세요.", HttpStatus.valueOf(400));

        user.updateMyInfo(requestDto);

        userRepository.save(user);

        return new ResponseEntity<>("변경이 완료 되었습니다.", HttpStatus.valueOf(200));
    }

    @Transactional
    public ResponseEntity<?> updateMyPassword(UpdateMyPasswordRequestDto requestDto, User user) {
        if(user.getPassword() == null || user.getPassword().equals(""))
            return new ResponseEntity<>("비밀번호를 입력해주세요.", HttpStatus.valueOf(400));

        if(requestDto.getPassword() == null || requestDto.getPassword().equals("") || requestDto.getConfirmChangePassword() == null || requestDto.getConfirmChangePassword().equals(""))
            return new ResponseEntity<>("비밀번호를 입력해주세요.", HttpStatus.valueOf(400));

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
    @Transactional
    public ResponseEntity<?> reIssueRefreshToken(HttpServletRequest request) {
        String accessToken = extractor.extract(request.getHeader("Authorization"), request);

        String refreshToken = request.getHeader("RefreshToken");

        String username = jwtDecoder.decodeUserToken(accessToken);

        Optional<Auth> findUserRefreshToken = authRepository.findByUsername(username);
        String token = null;
        if (findUserRefreshToken.isPresent()) {
            if (findUserRefreshToken.get().getRefreshToken().equals(refreshToken)) {
                if (jwtDecoder.isExpiredToken(refreshToken)) {
                    return new ResponseEntity<>("만료된 리프레쉬 토큰입니다.", HttpStatus.valueOf(401));
                } else {
                    // 새로운 엑세스 토큰 발급
                    token = JwtTokenUtils.generateJwtToken(username);
                    // 접속 유저의 엑세스 토큰 업데이트
                }
            } else {
                return new ResponseEntity<>("저장된 리프레쉬 토큰과 일치하지 않습니다.", HttpStatus.valueOf(401));
            }
        } else {
            return new ResponseEntity<>("존재하지 않는 리프레쉬 토큰입니다.", HttpStatus.valueOf(401));
        }
        return ResponseEntity.ok().header("Authorization","BEARER " + token).body("토큰 재발급");
    }

}
