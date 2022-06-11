package com.goup.repositories;

import com.goup.models.Like;
import com.goup.models.Post;
import com.goup.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository {
    Like findByPostAndUser(Post post, User user);
}
