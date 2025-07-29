package com.challenge.springsample.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.challenge.springsample.model.UserAccount;
import com.challenge.springsample.repository.UserAccountRepository;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    
    private UserAccountRepository userAccountRepository;

    public UserDetailsServiceImp(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByUsername(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("username does NOT exist: " + username);
        }
        return new User(
                userAccount.getUsername(),
                userAccount.getPassword(),
                Collections.emptyList()
        );
    }
}
