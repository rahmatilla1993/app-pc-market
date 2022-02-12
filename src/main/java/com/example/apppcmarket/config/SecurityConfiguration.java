package com.example.apppcmarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/product/**").hasAnyRole("SUPER_ADMIN", "OPERATOR", "MODERATOR")
                .antMatchers("/api/order/**").hasAnyRole("SUPER_ADMIN","OPERATOR")
                .antMatchers("/api/**").hasRole("SUPER_ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("super_admin").password(passwordEncoder().encode("admin")).roles("SUPER_ADMIN")
                .and()
                .withUser("moderator").password(passwordEncoder().encode("moderator")).roles("MODERATOR")//.authorities("READ_PRODUCTS", "READ_ONE_PRODUCT", "READ_PRODUCTS_BY_CATEGORY", "EDIT_PRODUCT","ADD_PRODUCT")
                .and()
                .withUser("operator").password(passwordEncoder().encode("operator")).roles("OPERATOR");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
