package ru.kai.homework.correction.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.kai.homework.correction.model.enums.TransactionStatus;

import java.util.UUID;

@FeignClient(name = "transactionServiceClient", url = "${t1.client-url}")
public interface TransactionServiceClient {

    @PostMapping("/retry_transaction")
    ResponseEntity<TransactionStatus> retryTransaction(
            @RequestBody UUID transactionId
    );

    @PostMapping("/authorize_transaction")
    ResponseEntity<Boolean> authorizeTransaction(
            @RequestBody UUID transactionId
    );
}
