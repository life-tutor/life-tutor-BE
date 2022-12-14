package com.example.lifetutor.config.security.oauth2;

import com.example.lifetutor.config.security.UserDetailsImpl;
import com.example.lifetutor.config.security.jwt.JwtTokenUtils;
import com.example.lifetutor.user.model.Auth;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.user.repositroy.AuthRepository;
import com.example.lifetutor.user.repositroy.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

//        login 성공한 사용자 목록.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> kakao_account = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        String username = (String) kakao_account.get("email");
        User user =  userRepository.findByUsername(username).get();

        String jwt = JwtTokenUtils.generateJwtToken(username);
        String refreshToken = JwtTokenUtils.generateRefreshToken();

        Optional<Auth> refreshTokenFound = authRepository.findByUsername(username);


        if(refreshTokenFound.isPresent()){
            refreshTokenFound.get().updateToken(refreshToken);
            authRepository.save(refreshTokenFound.get());
        }else{
            authRepository.save(new Auth(username,refreshToken));
        }

        String url = makeRedirectUrl(jwt,refreshToken);

        System.out.println("url: " + url);

        if (response.isCommitted()) {
            logger.debug("응답이 이미 커밋된 상태입니다. " + url + "로 리다이렉트하도록 바꿀 수 없습니다.");
            return;
        }
        getRedirectStrategy().sendRedirect(request, response, url);
    }

    private String makeRedirectUrl(String token,String refreshToken) {
        return UriComponentsBuilder.fromUriString("https://it-ing.co.kr/oauth2/redirect/"+"accessToken="+token+"&refreshToken="+refreshToken)
                .build().toUriString();
    }
}
