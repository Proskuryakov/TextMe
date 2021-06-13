package ru.vsu.cs.textme.backend.security;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.vsu.cs.textme.backend.db.model.AppRole;

@Configuration
@ComponentScan(value = "ru.vsu.cs.textme.backend")
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/admin/**").hasRole(AppRole.ADMIN.getContent())
                .antMatchers("/api/moder/**").hasRole(AppRole.MODER.getContent())
                .antMatchers("/api/user/**").hasRole(AppRole.USER.getContent())
                .antMatchers("/api/chat/**").hasRole(AppRole.USER.getContent())
                .antMatchers("/api/messenger/**").hasRole(AppRole.USER.getContent())
                .antMatchers("/api/file-message/**").hasRole(AppRole.USER.getContent())
                .antMatchers("/api/tag/**").hasRole(AppRole.USER.getContent())
                .antMatchers("/api/card/**").hasRole(AppRole.USER.getContent())
                .antMatchers("/api/auth/**").permitAll()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
