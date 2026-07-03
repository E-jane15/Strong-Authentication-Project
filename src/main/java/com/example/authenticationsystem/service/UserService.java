package com.example.authenticationsystem.service;

import com.example.authenticationsystem.entity.User;
import com.example.authenticationsystem.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository repository,
                       BCryptPasswordEncoder encoder) {

        this.repository = repository;
        this.encoder = encoder;
    }

    public String register(User user){

        if(repository.existsByUsername(user.getUsername()))
            return "Username already exists";

        if(repository.existsByEmail(user.getEmail()))
            return "Email already exists";

        user.setPassword(
                encoder.encode(user.getPassword())
        );

        repository.save(user);

        return "SUCCESS";
    }

    public User login(String username,String password){

        var optionalUser = repository.findByUsername(username);

        if(optionalUser.isEmpty())
            return null;

        User user = optionalUser.get();

        if(encoder.matches(password,user.getPassword()))
            return user;

        return null;
    }

}