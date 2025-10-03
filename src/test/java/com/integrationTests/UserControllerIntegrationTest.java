package com.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.tradeApplication.config.CustomUserDetails;
import com.trade.tradeApplication.entity.UserEntity;
import com.trade.tradeApplication.model.NewPassword;
import com.trade.tradeApplication.model.Role;
import com.trade.tradeApplication.model.UpdateUser;
import com.trade.tradeApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.trade.tradeApplication.TradeApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserEntity testUser;
    private Authentication userAuth;
    private String uniqueUsername;
    private String testUserPassword = "password123";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        testUser = new UserEntity();
        testUser.setUsername(uniqueUsername);
        testUser.setPassword(passwordEncoder.encode(testUserPassword));
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPhone("+79123456789");
        testUser.setRole(Role.USER);
        testUser.setImage("default.jpg");
        testUser = userRepository.save(testUser);

        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        userAuth = new UsernamePasswordAuthenticationToken(
                userDetails, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getCurrentUser_WhenAuthenticated_ShouldReturnUserInfo() throws Exception {
        setAuthentication(userAuth);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phone").value("+79123456789"))
                .andExpect(jsonPath("$.image").value("default.jpg"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void getCurrentUser_WhenNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateCurrentUser_WhenValidData_ShouldUpdateUser() throws Exception {
        setAuthentication(userAuth);

        UpdateUser updateUser = new UpdateUser();
        updateUser.setFirstName("Janee");
        updateUser.setLastName("Smithh");
        updateUser.setPhone("+79876543210");

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Janee"))
                .andExpect(jsonPath("$.lastName").value("Smithh"))
                .andExpect(jsonPath("$.phone").value("+79876543210"));

        UserEntity updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assert updatedUser.getFirstName().equals("Janee");
        assert updatedUser.getLastName().equals("Smithh");
        assert updatedUser.getPhone().equals("+79876543210");
    }

    @Test
    void updateCurrentUser_WhenInvalidFirstName_ShouldReturnBadRequest() throws Exception {
        setAuthentication(userAuth);

        UpdateUser updateUser = new UpdateUser();
        updateUser.setFirstName("Jo");
        updateUser.setLastName("Smith");
        updateUser.setPhone("+79876543210");

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCurrentUser_WhenInvalidPhone_ShouldReturnBadRequest() throws Exception {
        setAuthentication(userAuth);

        UpdateUser updateUser = new UpdateUser();
        updateUser.setFirstName("Jane");
        updateUser.setLastName("Smith");
        updateUser.setPhone("invalid-phone");

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void setPassword_WhenValidData_ShouldUpdatePassword() throws Exception {
        setAuthentication(userAuth);

        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword(testUserPassword);
        newPassword.setNewPassword("newPassword123");

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword)))
                .andExpect(status().isOk());


        UserEntity updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assert passwordEncoder.matches("newPassword123", updatedUser.getPassword());
    }

    @Test
    void setPassword_WhenInvalidNewPassword_ShouldReturnBadRequest() throws Exception {
        setAuthentication(userAuth);

        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword(testUserPassword);
        newPassword.setNewPassword("short");

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void setPassword_WhenTooLongNewPassword_ShouldReturnBadRequest() throws Exception {
        setAuthentication(userAuth);

        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword(testUserPassword);
        newPassword.setNewPassword("thispasswordistoolong123");

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserImage_WhenValidImage_ShouldUpdateImage() throws Exception {
        setAuthentication(userAuth);

        String newImage = "new-avatar.jpg";

        mockMvc.perform(patch("/users/me/image")
                        .param("image", newImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());


        UserEntity updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assert updatedUser.getImage().equals(newImage);
    }

    @Test
    void updateUserImage_WhenNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(patch("/users/me/image")
                        .param("image", "new-avatar.jpg")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUserImage_WhenMissingImageParameter_ShouldReturnBadRequest() throws Exception {
        setAuthentication(userAuth);

        mockMvc.perform(patch("/users/me/image")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateCurrentUser_WhenNullFields_ShouldReturnBadRequest() throws Exception {
        setAuthentication(userAuth);

        UpdateUser updateUser = new UpdateUser();


        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void setPassword_WhenNullFields_ShouldReturnBadRequest() throws Exception {
        setAuthentication(userAuth);

        NewPassword newPassword = new NewPassword();


        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateCurrentUser_ShouldNotUpdateEmail() throws Exception {
        setAuthentication(userAuth);

        UpdateUser updateUser = new UpdateUser();
        updateUser.setFirstName("Jane");
        updateUser.setLastName("Smith");
        updateUser.setPhone("+79876543210");

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk());

        UserEntity updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assert updatedUser.getUsername().equals(testUser.getUsername());
    }
}