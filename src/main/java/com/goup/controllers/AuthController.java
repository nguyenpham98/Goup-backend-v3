package com.goup.controllers;


import com.goup.models.User;
import com.goup.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value="/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> data){
        Map<String, String> response = new HashMap<>();
        User user = userRepository.findByEmail(data.get("email"));
        if (user != null){
            response.put("message", "Error! Someone already registered with that email.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        String hashedPassword = passwordEncoder.encode(data.get("password"));
        user = new User(data.get("email"), hashedPassword);
        try {
            userRepository.save(user);
        }
        catch (Exception e) {
            response.put("message", "Error! Could not register. Please try again.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        response.put("message", "Success! Please login using your new credentials.");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value="/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> data, HttpServletRequest httpServletRequest){
        Map<String, String> response = new HashMap<>();
        // check if user is already logged in
        Object userId = httpServletRequest.getSession().getAttribute("GOUP_ID");
        if (userId != null){
            response.put("message", "Already logged in.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        // check if user exists and password matches
        User user = userRepository.findByEmail(data.get("email"));
        if (user == null || !passwordEncoder.matches(data.get("password"), user.getPassword())) {
            response.put("message", "Error! Wrong email or password.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        // create new session when login success
        httpServletRequest.getSession().setAttribute("GOUP_ID", user.getId());
        response.put("message", "Success! Please login using your new credentials.");
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value="/me")
    public ResponseEntity<Map<String, String>> get_current_user(HttpServletRequest httpServletRequest){
        Map<String, String> response = new HashMap<>();
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
        user.fetchInfo(response);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value="/test")
    public String test(HttpServletRequest httpServletRequest){
        String token = (String) httpServletRequest.getSession().getAttribute("RANDOM_TOKEN");

        if (token == null) {
            System.out.println("No token yet. Creating one...");
            httpServletRequest.getSession().setAttribute("RANDOM_TOKEN", "Here's Johhny!");
        }
        else System.out.println(token);
        return "nice";
    }

}
