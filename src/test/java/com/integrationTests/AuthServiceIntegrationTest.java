package com.integrationTests;

import com.trade.tradeApplication.model.Register;
import com.trade.tradeApplication.entity.UserEntity;
import com.trade.tradeApplication.model.Role;
import com.trade.tradeApplication.repository.UserRepository;
import com.trade.tradeApplication.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.trade.tradeApplication.TradeApplication.class)
@ActiveProfiles("test")
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_WhenNewUser_ShouldReturnTrueAndSaveUser() {
        Register register = new Register();
        register.setUsername("newuser");
        register.setPassword("password123");
        register.setFirstName("John");
        register.setLastName("Doe");
        register.setPhone("+79123456789");
        register.setRole(Role.USER);

        boolean result = authService.register(register);

        assertTrue(result);

        UserEntity savedUser = userRepository.findByUsername("newuser");
        assertNotNull(savedUser);
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
    }

    @Test
    void login_WhenValidCredentials_ShouldReturnTrue() {

        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhone("+79123456789");
        user.setRole(Role.USER);
        userRepository.save(user);

        boolean result = authService.login("testuser", "password123");

        assertTrue(result);
    }
}
