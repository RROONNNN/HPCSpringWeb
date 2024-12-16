package com.example.HPCSpringWeb.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Setter
@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id=0;
    private String email;
    @Transient
    private String user_name_ssh;

    private String password_ssh;

    public User(){
//        System.out.println("User constructor");
//        this.user_name_ssh = "thuan";
//        this.password_ssh = "r@n22012004";
//        this.email = "tsarlvntn2004@gmail.com";
    }
    public User (String email, String user_name_ssh, String password_ssh) {
        this.email = email;
        this.user_name_ssh = user_name_ssh;
        this.password_ssh = password_ssh;
    }
public User(int id, String email,String password_ssh) {
    this.id = id;
    this.email = email;
    this.password_ssh = password_ssh;
    this.user_name_ssh = "uservbox"+id;
}

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }


    public String getUser_name_ssh() {
        if (user_name_ssh == null&& id!=0)
             user_name_ssh="uservbox"+id;
        return user_name_ssh;
    }

    public String getPassword_ssh() {
        return password_ssh;
    }
}
