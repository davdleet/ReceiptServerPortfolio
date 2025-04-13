package com.example.ReceiptServer.controller;

import com.example.ReceiptServer.config.TestSecurityConfig;
import com.example.ReceiptServer.dto.ApiResponse;
import com.example.ReceiptServer.dto.AuthResponse;
import com.example.ReceiptServer.entity.User;
import com.example.ReceiptServer.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.config.location=classpath:/application-test.properties")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User tempTestUser;

    @BeforeEach
    void setUp() {
        tempTestUser = new User("john_doe", "temp");
        userService.createUser(tempTestUser);
    }

    @Test
    void testCreateUser() {
        User newUser = new User("new_user", "temp");
        ResponseEntity<ApiResponse<AuthResponse>> response = userController.createUser(newUser);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("new_user", response.getBody().getData().getUsername());

        Optional<User> foundUser = userService.getUserByUsername("new_user");
        assertTrue(foundUser.isPresent());
    }

    @Test
    void testCreateUserDuplicateUsername() {
        User testUser = new User("john_doe", "temp");

        ResponseEntity<ApiResponse<AuthResponse>> response = userController.createUser(testUser);
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    void testGetAllUsers() {
        ResponseEntity<ApiResponse<List<User>>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertFalse(response.getBody().getData().isEmpty());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    void testGetUserByUsername_UserExists() {
        ResponseEntity<ApiResponse<User>> response = userController.getUserByUsername("john_doe");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals("john_doe", response.getBody().getData().getUsername());
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        ResponseEntity<ApiResponse<User>> response = userController.getUserByUsername("unknown_user");

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getData());
    }

    // Validation Tests Using MockMvc remain unchanged
    @Test
    void testCreateUser_InvalidUsername_TooShort() throws Exception {
        User invalidUser = new User("a", "temp");

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUser_InvalidUsername_TooLong() throws Exception {
        User invalidUser = new User("thisusernameiswaytoolong12345", "naver");

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUser_InvalidUsername_SpecialChars() throws Exception {
        User invalidUser = new User("!invalid@", "temp");

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUser_BlankUsername() throws Exception {
        User invalidUser = new User("", "temp");

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUser_InvalidAccountType() throws Exception {
        User invalidUser = new User("valid_user", "invalid_type");

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUser_ValidUsername() throws Exception {
        User validUser = new User("validUser123", "temp");

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk());
    }
}