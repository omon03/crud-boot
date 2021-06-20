package com.example.crud.service;

import java.util.List;

import com.example.crud.model.User;

public interface UserService {
    User getUserById(Long id);
    User saveUser(User user) throws Exception;
    User deleteUserById(Long id);
    List<User> showAllUsers();
    User updateUser(Long id, User user);
}
