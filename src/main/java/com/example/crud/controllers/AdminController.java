package com.example.crud.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.crud.model.Role;
import com.example.crud.model.User;
import com.example.crud.service.RoleServiceImpl;
import com.example.crud.service.UserServiceImpl;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserServiceImpl userService;
    private RoleServiceImpl roleService;

    @Autowired
    public AdminController(UserServiceImpl userService, RoleServiceImpl roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    public AdminController() {
    }

    @GetMapping()
    public String allUsers(Model model, Principal principal) {
        User user = userService.loadUserByUsername(principal.getName());
        model.addAttribute("currentuser", user);  // admin
        List<User> users = userService.showAllUsers();
        model.addAttribute("users", users);
        List<Role> roles = roleService.listRoles();
        model.addAttribute("roleS", roles);
        User newUser = new User();
        model.addAttribute("newuser", newUser);
        return "users";
    }

    @PostMapping("/newUser")
    public String saveUser(@ModelAttribute("newuser") User user,
                           @RequestParam(required = false, name = "roleId") Set<Long> roleId) throws Exception {
        user.setRoles(roleService.roleById(roleId));
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PatchMapping("{id}")
    public String update(@ModelAttribute("user") User user,
                         @PathVariable("id") Long id,
                         @RequestParam(required = false, name = "roleId") Set<Long> roleId){
        user.setRoles(roleService.roleById(roleId));
        userService.updateUser(id, user);
        return "redirect:/admin";
    }

    @DeleteMapping("{id}")
    public String delete(@ModelAttribute("user") User user,
                         @PathVariable("id") Long id){
        userService.deleteUserById(id);
        return "redirect:/admin";
    }
}
