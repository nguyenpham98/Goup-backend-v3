package com.goup.models;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="posts")
public class Post{

    @Id
    @GeneratedValue
    private int id;

    @NotEmpty
    @Column(name="content")
    private String content;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private User user;

    public Post(String content, User author) {
        this.content = content;
        this.user = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void fetchInfo(Map<String, String> response){
        response.put("id", String.valueOf(this.getId()));
        response.put("content", this.getContent());
        response.put("author", this.getUser().getUsername());
        response.put("create_date", String.valueOf(this.getCreateDate()));
    }
}
