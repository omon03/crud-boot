package com.example.crud.service;

import java.util.List;
import java.util.Set;

import com.example.crud.model.Role;

public interface RoleService {
    List<Role> listRoles();

    Set<Role> roleById(Set<Long> roleId);
}
