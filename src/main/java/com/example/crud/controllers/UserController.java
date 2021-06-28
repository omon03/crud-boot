package com.example.crud.controllers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.crud.service.RoleService;
import com.example.crud.service.RoleServiceImpl;
import com.example.crud.service.UserService;
import com.example.crud.service.UserServiceImpl;
import lombok.ToString;

@Controller
@ToString
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserServiceImpl userService, RoleServiceImpl roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String index() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return "redirect:/login";
        }
        if (auth.getAuthorities().contains(roleService.getRole("ROLE_ADMIN"))) {
            return "redirect:/admin";
        }
        return "redirect:/user";
    }

    @GetMapping("/user")
    public String user(HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        long id = userService.getUser(auth.getName()).getId();
        response.addCookie(new Cookie("UserId", "" + id));
        boolean isAdmin = userService.isAdmin(userService.getUser(auth.getName()));
        response.addCookie(new Cookie("isAdmin", "" + isAdmin));
        return "index";
    }

    @GetMapping("/admin")
    public String admin(HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        long id = userService.getUser(auth.getName()).getId();
        response.addCookie(new Cookie("UserId", "" + id));
        boolean isAdmin = userService.isAdmin(userService.getUser(auth.getName()));
        response.addCookie(new Cookie("isAdmin", "" + isAdmin));
        return "index";
    }
}
