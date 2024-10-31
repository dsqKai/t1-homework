package ru.kai.homework.correction.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "transactionServiceClient", url = "${t1.client-url}")
public interface TransactionServiceClient {

    @PostMapping("/retry_transaction")
    ResponseEntity<Void> retryTransaction(
            @RequestBody UUID transactionId,
            @RequestHeader("Authorization") String jwtToken
    );
}
