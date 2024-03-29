package com.example.movie_nights_rest.config.security.oAuth2;

import com.example.movie_nights_rest.config.security.UserPrincipal;
import com.example.movie_nights_rest.exception.OAuth2AuthenticationProcessingException;
import com.example.movie_nights_rest.model.AuthProvider;
import com.example.movie_nights_rest.model.Role;
import com.example.movie_nights_rest.model.User;
import com.example.movie_nights_rest.repository.UserRepository;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomOAuthUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuthUserInfo(
                userRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes());
        String refreshToken = userRequest.getAdditionalParameters().get("refresh_token").toString();
        String email = userInfo.getEmail();

        if (StringUtils.isEmpty(email))
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");

        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider().equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, userInfo, refreshToken);
        } else {
            user = registerNewUser(userRequest, userInfo, refreshToken);
        }

        return UserPrincipal.create(user);
    }

    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo, String refreshToken) {
        User user = new User();

        user.setProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setRefreshToken(refreshToken);

        var roles = new ArrayList<String>();
        roles.add(Role.BASIC);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo, String refreshToken) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setRefreshToken(refreshToken);
        return userRepository.save(existingUser);
    }
}
