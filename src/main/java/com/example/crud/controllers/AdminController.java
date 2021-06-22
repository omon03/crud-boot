package com.example.crud.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.crud.model.User;
import com.example.crud.service.RoleServiceImpl;
import com.example.crud.service.UserServiceImpl;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private UserServiceImpl userService;
    private RoleServiceImpl roleService;

    public AdminController() { }

    @Autowired
    public AdminController(UserServiceImpl userService, RoleServiceImpl roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String showTableOfUsers(Model model) {
        System.out.println("Access admin to page users");
        List<User> users = userService.showAllUsers();
        System.out.println(users);
        model.addAttribute("users", users);
        return "/admin/users";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allroles", roleService.listRoles());
        return "/admin/new";
    }

    @PostMapping("/new")
    public String createNewUser(@ModelAttribute("user") User user,
                                @RequestParam(required = false, name = "roleId") Set<Long> roleId) throws Exception {
        user.setRoles(roleService.roleById(roleId));
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable("id") long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "/user/user";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") long id) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("allroles", roleService.listRoles());
        return "/admin/edit";
    }

    @PostMapping("/user/{id}")
    public String update(@ModelAttribute("user") User user,
                         @PathVariable("id") long id,
                         @RequestParam(required = false, name = "roleId") Set<Long> roleId) {
        user.setRoles(roleService.roleById(roleId));
        userService.updateUser(id, user);
        return "redirect:/admin/users";
    }

    @GetMapping("/user/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/users";
    }
}
