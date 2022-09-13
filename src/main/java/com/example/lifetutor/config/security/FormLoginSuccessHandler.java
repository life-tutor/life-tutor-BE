package com.example.lifetutor.config.security;

import com.example.lifetutor.config.security.jwt.JwtTokenUtils;
import com.example.lifetutor.user.dto.response.LoginResponseDto;
import com.example.lifetutor.user.model.Auth;
import com.example.lifetutor.user.repositroy.AuthRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
@RequiredArgsConstructor
public class FormLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public static final String AUTH_HEADER = "Authorization";
    public static final String REFRESH_TOKEN = "RefreshToken";
    public static final String TOKEN_TYPE = "BEARER";
    private final AuthRepository authRepository;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();

        final UserDetailsImpl userDetails = ((UserDetailsImpl) authentication.getPrincipal());
        // Token 생성
        final String token = JwtTokenUtils.generateJwtToken(userDetails.getUsername());

        final String refreshToken = JwtTokenUtils.generateRefreshToken();
        // 같은 username 으로 등록 된 refresh Token 을 찾음.
        Optional<Auth> userToken = authRepository.findByUsername(userDetails.getUsername());

        // refresh Token 이 존재하지 않으면
        if (!userToken.isPresent()) {
            // 처음 회원가입 / 로그인 시
            // refreshToken 저장
            Auth auth = Auth.builder()
                    .username(userDetails.getUsername())
                    .refreshToken(refreshToken)
                    .build();
            authRepository.save(auth);
        } else {
            // 로그인을 다시 했는데, DB에 리프레시 토큰이 있으면 새로운 리프레시 토큰으로 재발행
            userToken.get().updateToken(refreshToken);
            Auth auth = Auth.builder()
                    .username(userDetails.getUsername())
                    .refreshToken(refreshToken)
                    .build();
            authRepository.save(auth);
        }

        LoginResponseDto loginResponseDto = new LoginResponseDto(userDetails.getUser().getUsername(),
                userDetails.getUser().getNickname(),
                userDetails.getUser().getUser_type(),
                userDetails.getUser().isKakao());

        response.addHeader(AUTH_HEADER, TOKEN_TYPE + " " + token);
        response.addHeader(REFRESH_TOKEN, refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(),loginResponseDto);

    }
}
