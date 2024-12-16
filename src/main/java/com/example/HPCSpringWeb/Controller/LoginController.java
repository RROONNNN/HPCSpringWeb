package com.example.HPCSpringWeb.Controller;

import com.example.HPCSpringWeb.Entity.User;
import com.example.HPCSpringWeb.Service.SshService;
import com.example.HPCSpringWeb.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SshService sshService;

    @GetMapping("/loginHPC")
    public String login() {
        return "login";
    }
    @GetMapping("/providePasswordSSH")
    private String providePasswordSSH(){
        return "providePasswordSSH";
    }
    @PostMapping("/verify-ssh-password")
    private String verifyPasswordSSH(@RequestParam("passwordSsh") String passwordSsh, Authentication authentication, HttpServletRequest request){
        String email = getName(authentication);
        System.out.println("In verifyPasswordSSH email: "+email);
        System.out.println("In verifyPasswordSSH passwordSsh: "+passwordSsh);
        User savedUser = userRepository.save(new User(email, null, passwordSsh));

        System.out.println("Saved user ID: " + savedUser.getId());
        savedUser.setUser_name_ssh("uservbox"+savedUser.getId());
        System.out.println("Saved user ID: " + savedUser.getUser_name_ssh());
        request.getSession().setAttribute("user",savedUser );
        try {
            sshService.addUser();
        } catch (IOException e) {
            userRepository.delete(savedUser);
            throw new RuntimeException(e);
        }

        return "redirect:/home";
    }
    private static String getName(Authentication authentication) {
        return Optional.of(authentication.getPrincipal())
                .filter(OidcUser.class::isInstance)
                .map(OidcUser.class::cast)
                .map(OidcUser::getEmail)
                .orElse(authentication.getName());
    }


}