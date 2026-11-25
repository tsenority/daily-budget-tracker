package com.budgettracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // When running this test, load application-test.properties instead of using only the default configuration.
class DailyBudgetTrackerApplicationTests {

	@Test
	void contextLoads() {
	}
}