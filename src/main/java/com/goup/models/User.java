package com.goup.models;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.*;

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
    @Column(name="email", unique=true)
    private String email;

    @NotEmpty
    @Column(name="password")
    private String password;

    @Column(name="about_me")
    private String about_me;

    @OneToMany(
            mappedBy="user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonBackReference
    private List<Post> posts;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "following")
    private Set<User> followers;

    @JoinTable(name = "followers",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "follower_id")})
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<User> following;
    public void addFollower(User toFollow) {
        following.add(toFollow);
        toFollow.getFollowers().add(this);
    }

    public void removeFollower(User toFollow) {
        following.remove(toFollow);
        toFollow.getFollowers().remove(this);
    }

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

    public void addPost(Post post) {
        posts.add(post);
        post.setUser(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setUser(null);
    }

    public void fetchInfo(Map<String, String> response){
        response.put("userId", String.valueOf(this.getId()));
        response.put("username", this.getUsername());
        response.put("email", this.getEmail());
        response.put("about_me", this.getAboutMe());
    }
}
