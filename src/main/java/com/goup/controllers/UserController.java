package com.goup.controllers;

import com.goup.ResourceNotFoundException;
import com.goup.models.Post;
import com.goup.models.User;
import com.goup.repositories.PostRepository;
import com.goup.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @PostMapping(value="/add-post", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String>> add_post(@RequestBody Map<String, String> data, HttpServletRequest httpServletRequest){
        Map<String, String> response = new HashMap<>();
        String content = data.get("body");
        // get user from client
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null){
            response.put("message", "Not logged in yet.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        int userId = Integer.valueOf(token.toString());
        User user = userRepository.findById(Integer.valueOf(userId)).orElse(null);
        if (user == null){
            response.put("message", "No user with that ID.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        // create post
        Post post = new Post(content);
        user.addPost(post);
        try {
            postRepository.save(post);
        }
        catch (Exception e){
            e.printStackTrace();
            response.put("message", "Snap! We couldn't save your post at the moment.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("message", "Success! Your post is now live.");
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value="/all-posts")
    public ResponseEntity<Map<String, List<Post>>> get_all_posts(HttpServletRequest httpServletRequest){
        Map<String, List<Post>> response = new HashMap<>();
        // check logged in first for fetching is_following property
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        int userId = Integer.valueOf(token.toString());
        User user = userRepository.findById(Integer.valueOf(userId)).orElse(null);
        if (user == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        // find all posts
        // TODO: fetch is_following property
        List<Post> posts = postRepository.findAll();
        response.put("posts", posts);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value="/my-profile")
    public ResponseEntity<Map<String, User>> get_my_profile(HttpServletRequest httpServletRequest){
        Map<String, User> response = new HashMap<>();
        // check logged in first
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        int userId = Integer.valueOf(token.toString());
        User user = userRepository.findById(Integer.valueOf(userId)).orElse(null);
        if (user == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        response.put("user", user);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value="/user-profile/{user_id}")
    public ResponseEntity<Map<String, User>> get_user_profile(@PathVariable(required=false,name="data") Integer user_id, HttpServletRequest httpServletRequest){
        Map<String, User> response = new HashMap<>();
        // check logged in first
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        int userId = Integer.valueOf(token.toString());
        User user = userRepository.findById(Integer.valueOf(userId)).orElse(null);
        if (user == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        // get another user profile
        User another_user = userRepository.findById(user_id).orElse(null);
        if (another_user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("user", another_user);
        // TODO: insert is_following into another_user
        return ResponseEntity.ok().body(response);
    }

//    @GetMapping("/users")
//    public ResponseEntity<List<User>> getAllUsers() {
//        return ResponseEntity.ok(userRepository.findAll());
//    }
//
//    @GetMapping("users/{id}")
//    public ResponseEntity<User> findUserById(@PathVariable(value = "id") Integer userId) {
//        User user = userRepository.findById(userId).orElseThrow(
//                () -> new ResourceNotFoundException("User not found" + userId));
//        return ResponseEntity.ok().body(user);
//    }
//
//
//    @DeleteMapping("users/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id") Integer userId) {
//        User user = userRepository.findById(userId).orElseThrow(
//                () -> new ResourceNotFoundException("User not found" + userId));
//        userRepository.delete(user);
//        return ResponseEntity.ok().build();
//    }
}
