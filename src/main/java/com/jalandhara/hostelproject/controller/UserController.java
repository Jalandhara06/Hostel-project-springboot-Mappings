package com.jalandhara.hostelproject.controller;

import com.jalandhara.hostelproject.entity.Users;
import com.jalandhara.hostelproject.service.JwtService;
import com.jalandhara.hostelproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/entry")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/")
    public String user(HttpServletRequest request) {
        return "Hello World!  " + request.getSession().getId();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Users user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user) {
        return userService.login(user);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");
        try {
            String username = jwtService.extractUserName(refreshToken);
            String newAccessToken = jwtService.generateAccessToken(username);
            Map<String, String> response = new HashMap<>();
            response.put("access_token", newAccessToken);
            response.put("refresh_token", refreshToken);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }
    }

    @GetMapping("/get-user")
    public List<Users> getUser(){
        return userService.getUSer();
    }

    @GetMapping("/home")
    public String home() {
        return "This is the home page";
    }

    @GetMapping("/sessionId")
    public String getSessionId(HttpServletRequest request) {
        return request.getSession().getId();
    }

    @GetMapping("/csrf-token-id")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

}
