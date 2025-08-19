package com.jalandhara.hostelproject.service;

import com.jalandhara.hostelproject.entity.Users;
import com.jalandhara.hostelproject.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public String registerUser(Users user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
        return user.getUsername() + " is registered successfully.";
    }

    public ResponseEntity<?> login(Users user) {
        try{
            Authentication authentication = authenticationManager
                    .authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            if (authentication.isAuthenticated()) {
                String accessToken = jwtService.generateAccessToken(user.getUsername());
                String refreshToken = jwtService.generateRefreshToken(user.getUsername());
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                return ResponseEntity.ok(tokens);
            } else {
                return ResponseEntity.status(401).body("Authentication Failed");
            }
        }catch (BadCredentialsException e){
            return ResponseEntity.status(401).body("Invalid username or password");
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(403).body("Username not found");
        }catch (Exception e){
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    public List<Users> getUSer(){
        return userRepo.findAll();
    }

}
