package com.goup.controllers;


import com.goup.models.User;
import com.goup.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping(value="/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> register(@RequestBody User user){
        Map<String, String> response = new HashMap<>();
        try {
            userRepository.save(user);
        }
        catch (Exception e) {
            response.put("message", "Error! Could not register. Please try again.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        response.put("message", "Success! Please login using your new credentials.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
