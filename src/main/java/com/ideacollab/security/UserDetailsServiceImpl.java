package com.ideacollab.security;

import com.ideacollab.model.User;
import com.ideacollab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with employeeId: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getHashedPassword(), // You should include the password
                user.getAuthorities() // Convert roles to authorities
        );
    }
}