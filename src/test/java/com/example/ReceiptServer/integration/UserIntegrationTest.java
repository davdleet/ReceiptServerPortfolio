package com.example.ReceiptServer.integration;
import com.example.ReceiptServer.config.TestSecurityConfig;
import com.example.ReceiptServer.entity.User;
import com.example.ReceiptServer.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.config.location=classpath:/application-test.properties")
@ActiveProfiles("test")  // ✅ Forces Spring Boot to use the test profile
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // ✅ Resets DB after each test
@Transactional  // ✅ Ensures changes are rolled back after each test
@Import(TestSecurityConfig.class)
class UserIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Clearing and flushing the repository helps initialize the schema
        userRepository.deleteAll();
        userRepository.flush();
    }
    
    @Test
    void testCreateAndFindUser() {
        User user = new User("john_doe", "temp");
        userRepository.save(user);

        List<User> users = userRepository.findAll();
        assertEquals(1, users.size());
        assertEquals("john_doe", users.get(0).getUsername());
    }
}
