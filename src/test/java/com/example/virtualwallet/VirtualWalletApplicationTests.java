package com.example.virtualwallet;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class VirtualWalletApplicationTests {

    @BeforeAll
    static void loadTestEnv() {
        Dotenv dotenv = Dotenv.configure()
                .filename(".env.test")
                .ignoreIfMissing()
                .load();


        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }

    @Test
    void contextLoads() {
    }

}
