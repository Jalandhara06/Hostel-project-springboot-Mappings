package com.jalandhara.hostelproject.service;

import com.jalandhara.hostelproject.entity.UserPrincipal;
import com.jalandhara.hostelproject.entity.Users;
import com.jalandhara.hostelproject.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepo.findByUsername(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException(username + " not found");
        }
        return new UserPrincipal(user);
    }
}
