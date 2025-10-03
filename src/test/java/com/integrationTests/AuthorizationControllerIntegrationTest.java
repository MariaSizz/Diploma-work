package com.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.tradeApplication.entity.UserEntity;
import com.trade.tradeApplication.model.Login;
import com.trade.tradeApplication.model.Register;
import com.trade.tradeApplication.model.Role;
import com.trade.tradeApplication.repository.AdRepository;
import com.trade.tradeApplication.repository.CommentRepository;
import com.trade.tradeApplication.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = com.trade.tradeApplication.TradeApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorizationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdRepository adRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
        adRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_WhenValidData_ShouldReturnCreated() throws Exception {
        Register register = new Register();
        register.setUsername("testuser");
        register.setPassword("password123");
        register.setFirstName("John");
        register.setLastName("Doe");
        register.setPhone("+79123456789");
        register.setRole(Role.USER);

        String requestBody = objectMapper.writeValueAsString(register);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    if (result.getResolvedException() != null) {
                        result.getResolvedException().printStackTrace();
                    }
                });
    }

    @Test
    void register_WhenUsernameAlreadyExists_ShouldReturnBadRequest() throws Exception {
        // Сначала создаем пользователя
        UserEntity existingUser = new UserEntity();
        existingUser.setUsername("existinguser");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setPhone("+79123456789");
        existingUser.setRole(Role.USER);
        userRepository.save(existingUser);

        Register register = new Register();
        register.setUsername("existinguser");
        register.setPassword("newpassword123");
        register.setFirstName("New");
        register.setLastName("User");
        register.setPhone("+79876543210");
        register.setRole(Role.USER);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WhenValidCredentials_ShouldReturnOk() throws Exception {

        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhone("+79123456789");
        user.setRole(Role.USER);
        userRepository.save(user);

        Login login = new Login();
        login.setUsername("testuser");
        login.setPassword("password123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk());
    }

    @Test
    void login_WhenInvalidUsername_ShouldReturnUnauthorized() throws Exception {
        Login login = new Login();
        login.setUsername("nonexistent");
        login.setPassword("password123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}
