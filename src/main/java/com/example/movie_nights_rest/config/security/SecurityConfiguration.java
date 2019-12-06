package com.example.movie_nights_rest.config.security;

import com.example.movie_nights_rest.config.security.oAuth.CustomOAuthUserService;
import com.example.movie_nights_rest.config.security.oAuth.HttpCookieOAuthAuthorizationRequestRepository;
import com.example.movie_nights_rest.config.security.oAuth.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final MyUserDetailsService myUserDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final HttpCookieOAuthAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final CustomOAuthUserService customOAuthUserService;

    public SecurityConfiguration(MyUserDetailsService myUserDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder,
                                 RestAuthenticationEntryPoint authenticationEntryPoint,
                                 HttpCookieOAuthAuthorizationRequestRepository cookieAuthorizationRequestRepository,
                                 CustomOAuthUserService customOAuthUserService) {

        this.myUserDetailsService = myUserDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.cookieAuthorizationRequestRepository = cookieAuthorizationRequestRepository;
        this.customOAuthUserService = customOAuthUserService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(myUserDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable()
                .formLogin().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
                .authorizeRequests().anyRequest().permitAll().and()
                .oauth2Login().authorizationEndpoint().baseUri("/oauth/authorize")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository).and()
                .userInfoEndpoint()
                .userService(customOAuthUserService)
                .and()

    }
}
