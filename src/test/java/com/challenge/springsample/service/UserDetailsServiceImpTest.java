package com.challenge.springsample.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;

import com.challenge.springsample.model.UserAccount;
import com.challenge.springsample.repository.UserAccountRepository;
import com.challenge.springsample.service.UserDetailsServiceImp;
import com.challenge.springsample.util.TestContainersInitializer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = TestContainersInitializer.class)
public class UserDetailsServiceImpTest {

    @Autowired
    private UserAccountRepository userAccountRepository;
    private UserDetailsServiceImp userDetailsService;

    private final String USERNAME_TEST = "testUser";
    private final String PASSWORD_TEST = "testPassword";

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImp(userAccountRepository);
        UserAccount userAccount = new UserAccount(null, USERNAME_TEST, PASSWORD_TEST);
        userAccountRepository.save(userAccount);
    }

    @Test
    void testLoadUserByUsername() {
        UserDetails userAccount = userDetailsService.loadUserByUsername(USERNAME_TEST);
        Assertions.assertThat(userAccount).isNotNull();
        Assertions.assertThat(userAccount.getUsername()).isEqualTo(USERNAME_TEST);
        Assertions.assertThat(userAccount.getPassword()).isEqualTo(PASSWORD_TEST);   
    }

    @Test
    void testLoadUserByUsernameNotExist() {
        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonExistentUser"),
                " if user does not exists we expect UsernameNotFoundException to be thrown"
        );
    }
}
