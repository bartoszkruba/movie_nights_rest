package com.example.movie_nights_rest.config.security;

import com.example.movie_nights_rest.model.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrincipal implements OAuth2User, UserDetails {

    @Getter
    private String id;
    @Getter
    private String email;
    private String password;
    @Builder.Default
    private Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
    @Builder.Default
    @Setter
    private Map<String, Object> attributes = new HashMap<>();

    public static UserPrincipal create(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .authorities(user.getRoles()
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())).build();
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes){
        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .attributes(attributes)
                .authorities(user.getRoles()
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())).build();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
