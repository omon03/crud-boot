package com.example.crud.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.crud.model.User;
import com.example.crud.service.UserServiceImpl;
import lombok.ToString;

@Controller
@RequestMapping("/user")
@ToString
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String showUser(Model model, Principal principal) {
        User user = userService.loadUserByUsername(principal.getName());
        model.addAttribute("user", user);
        return "/user/user";
    }
}
