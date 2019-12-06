package com.example.movie_nights_rest.config.security;

import com.example.movie_nights_rest.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenUtil jwtTokenUtil;

    public JwtRequestFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authToken = httpServletRequest.getHeader("Authorization").replace(TOKEN_PREFIX, "");

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
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
