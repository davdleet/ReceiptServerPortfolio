package com.example.ReceiptServer.service;


import com.example.ReceiptServer.config.TestSecurityConfig;
import com.example.ReceiptServer.entity.User;
import com.example.ReceiptServer.repository.UserRepository;
import com.example.ReceiptServer.service.user.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.config.location=classpath:/application-test.properties")
@ActiveProfiles("test")  // ✅ Forces Spring Boot to use the test profile
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // ✅ Resets DB after each test
@Transactional  // ✅ Ensures changes are rolled back after each test
@Import(TestSecurityConfig.class)
class UserServiceTest {

    @Autowired  // ✅ Use the real repository
    private UserRepository userRepository;

    @Autowired  // ✅ Use the real service
    private UserService userService;

    private User tempTestUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // ✅ Clear DB before each test
        userRepository.flush();
        tempTestUser = new User("john_doe", "temp");
    }


    @Test
    void testCreateUser() {
        User savedUser = userService.createUser(tempTestUser);
        assertNotNull(savedUser);
        assertEquals("john_doe", savedUser.getUsername());

        Optional<User> foundUser = userRepository.findByUsername("john_doe");
        assertTrue(foundUser.isPresent()); // ✅ Ensure user is saved in DB
    }

    @Test
    void testGetAllUsers() {
        userRepository.save(tempTestUser); // ✅ Save user in DB
        List<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());

        // add a new user
        User newUser = new User("new_user", "temp");
        userRepository.save(newUser);

        List<User> users2 = userService.getAllUsers();
        assertNotNull(users2);
        assertFalse(users2.isEmpty());
        assertEquals(2, users2.size());
    }

    @Test
    void testGetUserByUsername_UserExists() {
        userRepository.save(tempTestUser); // ✅ Save user in DB

        Optional<User> foundUser = userService.getUserByUsername("john_doe");
        assertTrue(foundUser.isPresent());
        assertEquals("john_doe", foundUser.get().getUsername());
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        Optional<User> foundUser = userService.getUserByUsername("unknown_user");
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testUsernameExists() {
        userRepository.save(tempTestUser); // ✅ Save user in DB

        assertTrue(userService.usernameExists("john_doe"));
    }

    @Test
    void testUsernameDoesNotExist() {
        assertFalse(userService.usernameExists("unknown_user"));
    }

    @Test
    void testCreateUserWithExistingUsername() {
        userRepository.save(tempTestUser); // ✅ Save user in DB

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(tempTestUser));
    }

    @Test
    void testCreateUserWithUniqueUsername() {
        User savedUser = userService.createUser(tempTestUser);
        assertNotNull(savedUser);
        assertEquals("john_doe", savedUser.getUsername());
    }

    @Test
    void testInvalidOAuthUser()
    {
        User oAuthUser = new User("john_doe", "kakao");
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userService.createUser(oAuthUser));
    }
}