package com.example.HPCSpringWeb;


import com.example.HPCSpringWeb.Entity.User;
import com.example.HPCSpringWeb.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserRepository userRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String email = getName(authentication);
        System.out.println("email: "+email);
        if(userRepository.findByEmail(email).isPresent()){
            System.out.println("User already exists");
         ;
           // user.setUser_name_ssh("uservbox"+user.getId());
            request.getSession().setAttribute("user",userRepository.findByEmail(email).get());
            response.sendRedirect("/home");
        }else{
            System.out.println("User not exists");
            // user must require to give password_ssh to login
            response.sendRedirect("/providePasswordSSH");
            //   userRepository.save(new User(email,"thuan","r@n22012004"));
        }
        //save user to session
    }
    private static String getName(Authentication authentication) {
        return Optional.of(authentication.getPrincipal())
                .filter(OidcUser.class::isInstance)
                .map(OidcUser.class::cast)
                .map(OidcUser::getEmail)
                .orElse(authentication.getName());

    }
}