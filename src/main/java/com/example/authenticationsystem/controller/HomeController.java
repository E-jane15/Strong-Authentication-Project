package com.example.authenticationsystem.controller;

import com.example.authenticationsystem.entity.User;
import com.example.authenticationsystem.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final UserService service;

    public HomeController(UserService service){
        this.service=service;
    }

    @GetMapping("/")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    @PostMapping("/register")
    public String register(User user,
                           Model model){

        String response = service.register(user);

        if(response.equals("SUCCESS")){

            model.addAttribute("message",
                    "Registration Successful. Login below.");

            return "login";
        }

        model.addAttribute("message",response);

        return "register";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model){

        User user = service.login(username,password);

        if(user==null){

            model.addAttribute("message",
                    "Invalid Username or Password");

            return "login";
        }

        model.addAttribute("username",
                user.getFullName());

        return "dashboard";

    }

}