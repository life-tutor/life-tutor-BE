package com.example.lifetutor.config.security.oauth2;

import com.example.lifetutor.user.model.Role;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.user.repositroy.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserOAuth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        System.out.println(attributes);

        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        String username = (String) kakao_account.get("email");

        if(username == null) {
            throw new AuthenticationException("이메일 제공을 동의해주세요"){};
        }

        if (!userRepository.findByUsername(username).isPresent()) {
            User user = new User(username, "User" + (int) (Math.random() * 1000000000), UUID.randomUUID().toString(), Role.SEEKER,true);
            userRepository.save(user);
        }

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")), attributes, "id");
    }
}
