package ru.kai.homework.client.aop;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kai.homework.client.exception.AccountNotFoundException;
import ru.kai.homework.client.exception.TransactionNotFoundException;
import ru.kai.homework.client.model.dto.response.MessageResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<MessageResponse> handleAccountNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Account not found"));
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<MessageResponse> handleTransactionNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Transaction not found"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
}
