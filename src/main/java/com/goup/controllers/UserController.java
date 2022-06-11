package com.goup.controllers;

import com.goup.models.Like;
import com.goup.models.Post;
import com.goup.models.User;
import com.goup.repositories.LikeRepository;
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

    @Autowired
    private LikeRepository likeRepository;

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
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
        if (user == null){
            response.put("message", "No user with that ID.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        // create post
        Post post = new Post(content, user);
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
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
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
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
        if (user == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        response.put("user", user);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping(value="/all-my-posts")
    public ResponseEntity<Map<String, List<Post>>> get_all_my_posts(HttpServletRequest httpServletRequest){
        Map<String, List<Post>> response = new HashMap<>();
        // check logged in first for fetching is_following property
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
        if (user == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        // find all posts
        List<Post> posts = user.getPosts();
        response.put("posts", posts);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value="/user-profile/{user_id}")
    public ResponseEntity<Map<String, User>> get_user_profile(@PathVariable(required=false,name="user_id") Integer user_id, HttpServletRequest httpServletRequest){
        Map<String, User> response = new HashMap<>();
        // check logged in first
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
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
    @GetMapping(value="/all-user-posts/{user_id}")
    public ResponseEntity<Map<String, List<Post>>> get_all_user_posts(@PathVariable(required=false,name="user_id") Integer user_id, HttpServletRequest httpServletRequest){
        Map<String, List<Post>> response = new HashMap<>();
        // check logged in first for fetching is_following property
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
        if (user == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        // find all posts
        // TODO: fetch is_following property
        User another_user = userRepository.findById(user_id).orElse(null);
        List<Post> posts = another_user.getPosts();
        response.put("posts", posts);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value="/edit-post", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> edit_post(@RequestBody Map<String, String> data, HttpServletRequest httpServletRequest){
        Map<String, String> response = new HashMap<>();
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null){
            response.put("message", "Error! Not logged in yet.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
        if (user == null){
            response.put("response","Error! No user exists.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        Post post = postRepository.findById(Integer.valueOf(data.get("id"))).orElse(null);
        post.setContent(data.get("content"));
        response.put("message", "Success! Your post is edited.");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value="/edit-profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> edit_profile(@RequestBody Map<String, String> data, HttpServletRequest httpServletRequest){
        Map<String, String> response = new HashMap<>();
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null){
            response.put("message", "Error! Not logged in yet.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
        if (user == null){
            response.put("response","Error! No user exists.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        if (!user.getAboutMe().equals(data.get("about_me"))) user.setAboutMe(data.get("about_me"));
        if (!user.getUsername().equals(data.get("username"))) user.setUsername((data.get("username")));
        response.put("message", "Success! Your profile has been updated." );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value="/delete-post", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> delete_post(@RequestBody Map<String, String> data, HttpServletRequest httpServletRequest){
        Map<String, String> response = new HashMap<>();
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null){
            response.put("message", "Error! Not logged in yet.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
        if (user == null){
            response.put("response","Error! No user exists.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        postRepository.deleteById(Integer.valueOf(data.get("id")));
        response.put("message", "Success! Your post is deleted.");
        return ResponseEntity.ok().body(response);
    }


    @PostMapping(value="/follow/{user_id}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public void follow(@PathVariable(required=false,name="user_id") Integer user_id, HttpServletRequest httpServletRequest){
        // check logged in first for fetching is_following property
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null) return;
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;
        User to_follow = userRepository.findById(user_id).orElse(null);
        user.addFollower(to_follow);
        userRepository.save(user);
    }

    @PostMapping(value="/unfollow/{user_id}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public void unfollow(@PathVariable(required=false,name="user_id") Integer user_id, HttpServletRequest httpServletRequest){
        // check logged in first for fetching is_following property
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null) return;
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;
        User to_unfollow = userRepository.findById(user_id).orElse(null);
        user.removeFollower(to_unfollow);
        userRepository.save(user);
    }

    @PostMapping(value="/like/{post_id}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public void like(@PathVariable(required=false,name="post_id") Integer post_id, HttpServletRequest httpServletRequest){
        // check logged in first for fetching is_following property
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null) return;
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        Post post = postRepository.findById(post_id).orElse(null);
        Like like = new Like(user, post);
        user.like(post, like);
    }

    @PostMapping(value="/unlike/{post_id}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public void unlike(@PathVariable(required=false,name="post_id") Integer post_id, HttpServletRequest httpServletRequest){
        // check logged in first for fetching is_following property
        Object token = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (token == null) return;
        int userId = Integer.parseInt(token.toString());
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        Post post = postRepository.findById(post_id).orElse(null);
        Like like = likeRepository.findByPostAndUser(post, user);
        user.unlike(post, like);
    }

}
