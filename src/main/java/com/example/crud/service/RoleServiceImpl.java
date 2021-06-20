package com.example.crud.service;

import java.util.List;

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
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }
}
