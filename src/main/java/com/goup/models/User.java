package com.goup.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue
    private int id;

    @Column(name="username")
    private String username;

    @NotEmpty
    @Column(name="email")
    private String email;

    @NotEmpty
    @Column(name="password")
    private String password;

    @Column(name="about_me")
    private String about_me;

    public User(String email, String password) {
        this.email=email;
        this.password=password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAboutMe() {
        return about_me;
    }

    public void setAboutMe(String about_me) {
        this.about_me = about_me;
    }

    public void fetchInfo(Map<String, String> response){
        response.put("username", this.getUsername());
        response.put("email", this.getEmail());
        response.put("about_me", this.getAboutMe());
    }
}
