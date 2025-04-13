package com.example.ReceiptServer;

import com.example.ReceiptServer.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = "spring.config.location=classpath:/application-test.properties")
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")  // ✅ Forces Spring Boot to use the test profile
class ReceiptServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
