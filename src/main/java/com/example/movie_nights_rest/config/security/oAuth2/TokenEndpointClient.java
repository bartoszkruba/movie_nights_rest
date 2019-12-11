package com.example.movie_nights_rest.config.security.oAuth2;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@Component
public class TokenEndpointClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {


    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {


        ClientRegistration clientRegistration = authorizationGrantRequest.getClientRegistration();
        AuthorizationCode authorizationCode = new AuthorizationCode(authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());
        URI redirectUri = toURI(authorizationGrantRequest.getAuthorizationExchange().getAuthorizationRequest().getRedirectUri());
        AuthorizationGrant authorizationCodeGrant = new AuthorizationCodeGrant(authorizationCode, redirectUri);
        URI tokenUri = toURI(clientRegistration.getProviderDetails().getTokenUri());
        ClientID clientId = new ClientID(clientRegistration.getClientId());
        Secret clientSecret = new Secret(clientRegistration.getClientSecret());
        Object clientAuthentication;
        if (ClientAuthenticationMethod.POST.equals(clientRegistration.getClientAuthenticationMethod())) {
            clientAuthentication = new ClientSecretPost(clientId, clientSecret);
        } else {
            clientAuthentication = new ClientSecretBasic(clientId, clientSecret);
        }

        TokenResponse tokenResponse;

        try {
            TokenRequest tokenRequest = new TokenRequest(tokenUri, (ClientAuthentication) clientAuthentication, authorizationCodeGrant);
            HTTPRequest httpRequest = tokenRequest.toHTTPRequest();
            httpRequest.setAccept("application/json");
            httpRequest.setConnectTimeout(30000);
            httpRequest.setReadTimeout(30000);
            tokenResponse = TokenResponse.parse(httpRequest.send());
        } catch (IOException | ParseException var19) {
            OAuth2Error oauth2Error = new OAuth2Error("invalid_token_response", "An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response: " + var19.getMessage(), (String) null);
            throw new OAuth2AuthorizationException(oauth2Error, var19);
        }

        if (!tokenResponse.indicatesSuccess()) {
            TokenErrorResponse tokenErrorResponse = (TokenErrorResponse) tokenResponse;
            ErrorObject errorObject = tokenErrorResponse.getErrorObject();
            OAuth2Error oauth2Error;
            if (errorObject == null) {
                oauth2Error = new OAuth2Error("server_error");
            } else {
                oauth2Error = new OAuth2Error(errorObject.getCode() != null ? errorObject.getCode() : "server_error", errorObject.getDescription(), errorObject.getURI() != null ? errorObject.getURI().toString() : null);
            }

            throw new OAuth2AuthorizationException(oauth2Error);
        } else {
            AccessTokenResponse accessTokenResponse = (AccessTokenResponse) tokenResponse;

            String accessToken = accessTokenResponse.getTokens().getAccessToken().getValue();
            OAuth2AccessToken.TokenType accessTokenType = null;
            if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(accessTokenResponse.getTokens().getAccessToken().getType().getValue())) {
                accessTokenType = OAuth2AccessToken.TokenType.BEARER;
            }

            long expiresIn = accessTokenResponse.getTokens().getAccessToken().getLifetime();
            LinkedHashSet scopes;
            if (CollectionUtils.isEmpty(accessTokenResponse.getTokens().getAccessToken().getScope())) {
                scopes = new LinkedHashSet(authorizationGrantRequest.getAuthorizationExchange().getAuthorizationRequest().getScopes());
            } else {
                scopes = new LinkedHashSet(accessTokenResponse.getTokens().getAccessToken().getScope().toStringList());
            }

            String refreshToken = null;
            if (accessTokenResponse.getTokens().getRefreshToken() != null) {
                refreshToken = accessTokenResponse.getTokens().getRefreshToken().getValue();
            }

            Map<String, Object> additionalParameters = new LinkedHashMap(accessTokenResponse.getCustomParameters());
            additionalParameters.put("refresh_token", refreshToken);
            return OAuth2AccessTokenResponse.withToken(accessToken).tokenType(accessTokenType).expiresIn(expiresIn).scopes(scopes).refreshToken(refreshToken).additionalParameters(additionalParameters).build();
        }
    }

    private static URI toURI(String uriStr) {
        try {
            return new URI(uriStr);
        } catch (Exception var2) {
            throw new IllegalArgumentException("An error occurred parsing URI: " + uriStr, var2);
        }
    }
}
