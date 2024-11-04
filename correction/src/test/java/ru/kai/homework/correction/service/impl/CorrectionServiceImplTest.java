package ru.kai.homework.correction.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.kai.homework.correction.feign.TransactionServiceClient;
import ru.kai.homework.correction.kafka.CorrectionProducer;
import ru.kai.homework.correction.model.Correction;
import ru.kai.homework.correction.model.enums.CorrectionStatus;
import ru.kai.homework.correction.model.enums.TransactionStatus;
import ru.kai.homework.correction.repository.CorrectionRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CorrectionServiceImplTest {

    @Mock
    private CorrectionRepository repository;

    @Mock
    private TransactionServiceClient transactionServiceClient;

    @Mock
    private CorrectionProducer producer;

    @InjectMocks
    private CorrectionServiceImpl correctionService;

    private UUID transactionId;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        correctionService.maxAttempts = 3;
        correctionService.pageSize = 1000;
    }

    @Test
    void newCorrection_ShouldSaveCorrection_WhenAttemptsLessThanMaxAndResponseIsError() {
        Correction correction = new Correction(transactionId, 2, CorrectionStatus.PENDING);
        given(repository.findById(transactionId)).willReturn(Optional.of(correction));
        given(transactionServiceClient.retryTransaction(transactionId))
                .willReturn(ResponseEntity.ok(TransactionStatus.ERROR));

        correctionService.newCorrection(transactionId);

        assertEquals(3, correction.getAttempts());
        verify(repository).save(correction);
    }

    @Test
    void newCorrection_ShouldDeleteCorrection_WhenResponseIsNotError() {
        Correction correction = new Correction(transactionId, 1, CorrectionStatus.PENDING);
        given(repository.findById(transactionId)).willReturn(Optional.of(correction));
        given(transactionServiceClient.retryTransaction(transactionId))
                .willReturn(ResponseEntity.ok(TransactionStatus.PROCESSED));

        correctionService.newCorrection(transactionId);

        verify(repository).deleteById(transactionId);
    }

    @Test
    void authorizeTransaction_ShouldReturnTrue_WhenAuthorizationIsSuccessful() {
        given(transactionServiceClient.authorizeTransaction(transactionId))
                .willReturn(ResponseEntity.ok(true));

        boolean result = correctionService.authorizeTransaction(transactionId);

        assertTrue(result);
    }

    @Test
    void authorizeTransaction_ShouldReturnFalse_WhenAuthorizationIsUnsuccessful() {
        given(transactionServiceClient.authorizeTransaction(transactionId))
                .willReturn(ResponseEntity.ok(false));

        boolean result = correctionService.authorizeTransaction(transactionId);

        assertFalse(result);
    }

    @Test
    void retryFailedTransactions_ShouldProcessCorrections_WhenAttemptsAreBelowMax() {
        Correction correction1 = new Correction(transactionId, 1, CorrectionStatus.PENDING);
        Page<Correction> page = new PageImpl<>(List.of(correction1));
        given(repository.findByStatus(CorrectionStatus.PENDING, PageRequest.of(0, 1000))).willReturn(page);

        correctionService.retryFailedTransactions();

        verify(producer).sendMessage(correction1);
        assertEquals(CorrectionStatus.SUCCESS, correction1.getStatus());
        verify(repository).save(correction1);
    }

    @Test
    void retryFailedTransactions_ShouldSetStatusToError_WhenProducerFails() {
        Correction correction1 = new Correction(transactionId, 1, CorrectionStatus.PENDING);
        Page<Correction> page = new PageImpl<>(List.of(correction1));
        given(repository.findByStatus(CorrectionStatus.PENDING, PageRequest.of(0, 1000))).willReturn(page);
        doThrow(new RuntimeException()).when(producer).sendMessage(correction1);

        correctionService.retryFailedTransactions();

        assertEquals(CorrectionStatus.ERROR, correction1.getStatus());
        verify(repository).save(correction1);
    }
}

