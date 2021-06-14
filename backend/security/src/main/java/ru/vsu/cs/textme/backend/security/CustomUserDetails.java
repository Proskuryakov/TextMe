package ru.vsu.cs.textme.backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.vsu.cs.textme.backend.db.model.AppRole;
import ru.vsu.cs.textme.backend.db.model.User;
import ru.vsu.cs.textme.backend.services.exception.UserForbiddenException;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails, Principal {
    private Collection<? extends GrantedAuthority> grantedAuthorities;
    private User user;

    public static CustomUserDetails toCustomUserDetails(User user) {
        CustomUserDetails c = new CustomUserDetails();
        if (user != null) {
            c.user = user;
            c.grantedAuthorities = user
                    .getRoles()
                    .stream()
                    .map((Function<AppRole, GrantedAuthority>) appRole -> new SimpleGrantedAuthority(appRole.getContent()))
                    .collect(Collectors.toList());
        }
        return c;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user == null ? Collections.emptyList() :grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return user == null ? "" :user.getPassword();
    }

    @Override
    public String getUsername() {
        return  user == null ? "" :user.getNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user == null || user.getBlocked() == null;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getName() {
        return user == null ? "" : user.getNickname();
    }

    @Override
    public boolean implies(Subject subject) {
        return true;
    }
}
