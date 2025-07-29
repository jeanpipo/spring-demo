package com.challenge.springsample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.springsample.jwt.JwtUtil;
import com.challenge.springsample.model.UserAccount;
import com.challenge.springsample.repository.UserAccountRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<Void> authenticateUser(@RequestBody UserAccount userAccount) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userAccount.getUsername(),
                        userAccount.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).header("algolia-token", token).build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody UserAccount userAccount) {
        if (userAccountRepository.existsByUsername(userAccount.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        userAccount.setPassword(encoder.encode(userAccount.getPassword()));
        userAccountRepository.save(userAccount);
        return ResponseEntity.ok().build();
    }
}