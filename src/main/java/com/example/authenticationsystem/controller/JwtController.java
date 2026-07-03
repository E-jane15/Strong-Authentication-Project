package com.example.authenticationsystem.controller;

import com.example.authenticationsystem.entity.AuthenticationRequest;
import com.example.authenticationsystem.entity.AuthenticationResponse;
import com.example.authenticationsystem.jwt.JwtUtil;
import com.example.authenticationsystem.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class JwtController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public JwtController(UserService userService,
                         JwtUtil jwtUtil) {

        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public AuthenticationResponse login(
            @RequestBody AuthenticationRequest request) {

        var user = userService.login(
                request.getUsername(),
                request.getPassword());

        if (user == null) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return new AuthenticationResponse(token);
    }
}