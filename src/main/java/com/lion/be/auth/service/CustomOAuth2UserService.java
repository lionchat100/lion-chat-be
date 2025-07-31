package com.lion.be.auth.service;

import com.lion.be.auth.domain.OAuth2Attributes;
import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.service.UserConvertor;
import com.lion.be.user.service.UserReadService;
import com.lion.be.user.service.UserWriteService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserReadService userReadService;
    private final UserWriteService userWriteService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = service.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, userNameAttributeName,
                oAuth2User.getAttributes());
        Map<String, Object> loginResult = login(attributes);
        User user = (User) loginResult.get("user");
        boolean isNewUser = (boolean) loginResult.get("isNewUser");

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                oAuth2User.getAttributes(),
                isNewUser
        );
    }

    private Map<String, Object> login(OAuth2Attributes attributes) {
        User user;
        boolean isNewUser;

        try {
            user = userReadService.fetchByEmail(attributes.getEmail());
            isNewUser = false;
            log.info("{} 유저 인식 완료.", attributes.getName());
        } catch (UsernameNotFoundException e) {
            user = UserConvertor.attributesToUser(attributes);
            userWriteService.save(user);
            isNewUser = true;
            log.info("{} 유저 정보 저장 완료.", attributes.getName());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("isNewUser", isNewUser);
        return result;
    }

}
