package com.example.crud.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.crud.model.Role;
import com.example.crud.repos.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService{

    @PersistenceContext
    private EntityManager em;
    RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Set<Role> getRoles(Set<Long> roleId) {
        try {
            return (Set<Role>) roleRepository.findAllById(roleId);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    @Override
    public Role getRole(Long id) {
        return roleRepository.getRoleById(id);
    }

    @Override
    public Role getRole(String role) {
        return roleRepository.getRoleByRole(role);
    }
}
