package com.example.movie_nights_rest.config.security.oAuth2;

import com.example.movie_nights_rest.exception.OAuth2AuthenticationProcessingException;
import com.example.movie_nights_rest.model.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuthUserInfo(String registrationId, Map<String, Object> attributes) {

        if (registrationId.equalsIgnoreCase(AuthProvider.google.toString()))
            return new GoogleOAuth2UserInfo(attributes);
        else
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
    }

}
