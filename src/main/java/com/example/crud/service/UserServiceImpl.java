package com.example.crud.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.crud.model.Role;
import com.example.crud.model.User;
import com.example.crud.repos.UserRepository;
import lombok.ToString;

@Service
@ToString
public class UserServiceImpl implements UserService, UserDetailsService {

    @PersistenceContext
    private EntityManager em;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder
                          ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param userName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public User loadUserByUsername(String userName) {
        User user = userRepository.findByUsername(userName);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> userFromDb = userRepository.findById(id);
        return userFromDb.orElse(new User());
    }

    @Override
    public List<User> showAllUsers() {
        return userRepository.findAll();
    }

    /**
     * создание нового пользователя
     * @param user
     * @return
     */
    @Override
    public User saveUser(User user) throws Exception {
        User newUser = new User();
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if ( userFromDB == null ) {
            throw new Exception(
                "There is an account with that email adress:"
                + user.getUsername());
        }

        newUser.setName(user.getName());
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEmail(user.getEmail());
        newUser.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));

        return userRepository.save(newUser);
    }

    @Override
    public User deleteUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return user.get();
        }
        return null;
    }

    @Override
    public User updateUser(Long id, User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return null;
        }

        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> usergtList(Long idMin) {
        return em
            .createQuery("SELECT u FROM User u WHERE u.id > :paramId", User.class)
            .setParameter("paramId", idMin).getResultList();
    }
}
