package com.example.HPCSpringWeb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final OAuth2LoginSuccessHandler successHandler;
    @Autowired
    public SecurityConfig(OAuth2LoginSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return http.authorizeHttpRequests(
        authorizeConfig->{
            authorizeConfig.requestMatchers("/").authenticated();
            authorizeConfig.requestMatchers("/home").authenticated();
            authorizeConfig.anyRequest().permitAll();
        }
        ).formLogin(form -> form.loginPage("/loginHPC").permitAll())
            .httpBasic(Customizer.withDefaults())//return a httpSecurity
            .oauth2Login(oauth2 -> oauth2.successHandler(successHandler))
            .csrf(form->form.disable())
            .build();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.builder()
                        .username("admin")
                        .password("{noop}admin")
                        .authorities("ROLE_admin")
                        .build()
        );
    }

}
