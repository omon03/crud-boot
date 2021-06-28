package com.example.crud.service;

import java.util.List;
import java.util.Set;

import com.example.crud.model.Role;

public interface RoleService {
    List<Role> getRoles();
    Set<Role> getRoles(Set<Long> roleId);
    Role getRole(Long id);
    Role getRole(String role);
}
