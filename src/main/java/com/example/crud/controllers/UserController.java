package com.example.crud.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.crud.service.UserServiceImpl;
import lombok.ToString;

@Controller
@RequestMapping("/user")
@ToString
class UserController {

    private UserServiceImpl userService;

    @Autowired
    UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping()
    //public String showUser(Model model, @ModelAttribute("user") User user) {
    public String showUser(Model model, Authentication authentication) {
        model.addAttribute("user", userService.loadUserByUsername(authentication.getName()));
        return "user/user";
    }
}
