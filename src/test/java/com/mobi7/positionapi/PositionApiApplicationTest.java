package com.mobi7.positionapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PositionApiApplicationTest {

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully
    }

    @Test
    void mainMethodRuns() {
        PositionApiApplication.main(new String[]{});
    }
}
