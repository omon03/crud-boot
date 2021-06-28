package com.example.crud.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.crud.model.Role;
import com.example.crud.model.User;
import com.example.crud.service.RoleService;
import com.example.crud.service.UserService;
import com.google.gson.Gson;

@RestController
@RequestMapping("/rest")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> read() {
        try {
            List<User> all_users = userService.showAllUsers();
            return (all_users != null) && (!all_users.isEmpty())
                ? new ResponseEntity<>(all_users, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> read(@PathVariable("id") long id, Principal principal) {
        User admin = userService.getUser(principal.getName());
        long authId = admin.getId();
        if (userService.isAdmin(admin) || id == authId) {  // условие доступа к данным
            try {
                User user = userService.getUser(id);
                return user != null
                    ? new ResponseEntity<>(user, HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/users")
    public ResponseEntity<?> create(@RequestBody User user) {
        try {
            userService.getUser(user.getEmail());
            return new ResponseEntity<>("Specified email is busy", HttpStatus.CONFLICT);
        } catch (Exception e) {/*ignore*/}

        try {
            userService.saveUser(user);
            User addedUser = userService.getUser(user.getEmail());
            return new ResponseEntity<>(addedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id,
                                    @RequestBody User user) {
        try {
            if (userService.getUser(user.getEmail()).getId() != id) {
                return new ResponseEntity<>("Specified email is busy", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {/*ignore*/}

        try {
            user.setId(id);
            userService.updateUser(user.getId(), user);
            User updatedUser = userService.getUser(id);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            return userService.deleteUserById(id) != null
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
