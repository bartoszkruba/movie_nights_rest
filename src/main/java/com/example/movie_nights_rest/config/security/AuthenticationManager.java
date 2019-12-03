package com.example.movie_nights_rest.config.security;

import com.example.movie_nights_rest.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtTokenUtil jwtTokenUtil;

    public AuthenticationManager(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String username;
        try {
            username = jwtTokenUtil.getUsernameFromToken(authToken);
        } catch (Exception e) {
            username = null;
        }

        // TODO: 2019-12-03 Check for expiration
        if (username != null) {
            Claims claims = jwtTokenUtil.getAllClaimsFromToken(authToken);
            List<String> roles = claims.get("scopes", List.class);
            List authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            var auth = new UsernamePasswordAuthenticationToken(username, username, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return Mono.just(auth);
        } else {
            return Mono.empty();
        }
    }

}
