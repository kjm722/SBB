package com.example.sbbTest.oauth;

import com.example.sbbTest.user.SiteUser;
import com.example.sbbTest.user.UserRepository;
import com.example.sbbTest.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class OAuthUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        log.info("getAttributes : {}",oAuth2User.getAttributes());

        String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        OAuthUserInfo oAuthUserInfo = null;

        if (provider.equals("google")) {
            log.info("구글 로그인");
            oAuthUserInfo = new GoogleUserDetails(oAuth2User.getAttributes());
        }

        String providerId = oAuthUserInfo.getProviderId();
        String email = oAuthUserInfo.getEmail();
        String username = oAuthUserInfo.getUsername();
        String loginId = provider + "_" + providerId;

        Optional<SiteUser> os = userRepository.findByusername(username);
        SiteUser siteUser;
        if (os.isEmpty()){
            siteUser = SiteUser.builder()
                    .username(loginId)
                    .email(email)
                    .provider(provider)
                    .providerId(providerId)
                    .role(UserRole.USER)
                    .build();

            userRepository.save(siteUser);
        } else {
            siteUser = os.get();
        }

        return new OAuthUserDetails(siteUser,oAuth2User.getAttributes());
    }
}