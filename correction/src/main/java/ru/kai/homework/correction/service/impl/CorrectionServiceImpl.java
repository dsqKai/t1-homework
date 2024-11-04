package ru.kai.homework.correction.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kai.homework.correction.feign.TransactionServiceClient;
import ru.kai.homework.correction.kafka.CorrectionProducer;
import ru.kai.homework.correction.model.Correction;
import ru.kai.homework.correction.model.enums.CorrectionStatus;
import ru.kai.homework.correction.model.enums.TransactionStatus;
import ru.kai.homework.correction.repository.CorrectionRepository;
import ru.kai.homework.correction.service.CorrectionService;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CorrectionServiceImpl implements CorrectionService {
    private final CorrectionRepository repository;
    private final TransactionServiceClient transactionServiceClient;
    private final CorrectionProducer producer;

    @Value("${t1.retry.max-attempts:3}")
    public int maxAttempts;

    @Value("${t1.retry.page-size:1000}")
    public int pageSize;

    @Transactional
    public void newCorrection(UUID transactionId) {
        Correction correction = repository.findById(transactionId)
                .orElse(new Correction(transactionId, 0, CorrectionStatus.PENDING));
        correction.setStatus(CorrectionStatus.PENDING);
        if (correction.getAttempts() < maxAttempts) {
            ResponseEntity<TransactionStatus> response = transactionServiceClient.retryTransaction(transactionId);
            correction.incrementAttempts();
            if(response.getBody() == TransactionStatus.ERROR) {
                repository.save(correction);
            } else {
                repository.deleteById(transactionId);
            }
        }
    }

    public boolean authorizeTransaction(UUID transactionId) {
        Boolean authorized = transactionServiceClient.authorizeTransaction(transactionId).getBody();
        return authorized != null && authorized;
    }

    @Transactional
    @Scheduled(fixedRateString = "${t1.retry.interval:60000}", initialDelayString = "${t1.retry.initialDelay:60000}")
    public void retryFailedTransactions() {
        int page = 0;
        Page<Correction> correctionPage;
        do {
            correctionPage = repository.findByStatus(CorrectionStatus.PENDING, PageRequest.of(page, pageSize));
            correctionPage.getContent().stream()
                    .filter(correction -> correction.getAttempts() < maxAttempts)
                    .forEach(correction -> {
                        correction.incrementAttempts();
                        try {
                            producer.sendMessage(correction);
                            correction.setStatus(CorrectionStatus.SUCCESS);
                        } catch (Exception e) {
                            correction.setStatus(CorrectionStatus.ERROR);
                        }
                        repository.save(correction);
                    });
            page++;
        } while (!correctionPage.isLast());
    }

}
