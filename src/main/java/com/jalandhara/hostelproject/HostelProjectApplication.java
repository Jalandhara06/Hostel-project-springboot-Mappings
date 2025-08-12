package com.jalandhara.hostelproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class HostelProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(HostelProjectApplication.class, args);
    }

}
