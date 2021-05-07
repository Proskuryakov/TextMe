package ru.vsu.cs.textme.backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.vsu.cs.textme.backend.db.model.AppRole;
import ru.vsu.cs.textme.backend.db.model.User;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private String nickname;
    private String password;
    private Collection<? extends GrantedAuthority> grantedAuthorities;

    public static CustomUserDetails toCustomUserDetails(User user) {
        CustomUserDetails c = new CustomUserDetails();
        c.nickname = user.getNickname();
        c.password = user.getPassword();

        c.grantedAuthorities = user
                .getRoles()
                .stream()
                .map((Function<AppRole, GrantedAuthority>) appRole -> new SimpleGrantedAuthority(appRole.getContent()))
                .collect(Collectors.toList());
        return c;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return nickname;
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
}
