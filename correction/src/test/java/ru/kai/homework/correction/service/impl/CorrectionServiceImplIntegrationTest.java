package ru.kai.homework.correction.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWireMock(port = 8081)
@SpringBootTest
@ActiveProfiles("test")
class CorrectionServiceImplIntegrationTest {
    @Autowired
    private CorrectionServiceImpl correctionService;

    @Test
    void testAuthorizeTransactionAllowed() {
        stubFor(post(urlEqualTo("/authorize_transaction"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));

        boolean isAuthorized = correctionService.authorizeTransaction(UUID.randomUUID());
        assertTrue(isAuthorized);
    }

    @Test
    void testAuthorizeTransactionNotAllowed() {
        stubFor(post(urlEqualTo("/authorize_transaction"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("false")));

        boolean isAuthorized = correctionService.authorizeTransaction(UUID.randomUUID());
        assertFalse(isAuthorized);
    }
}
