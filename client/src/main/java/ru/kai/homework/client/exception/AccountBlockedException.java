package ru.kai.homework.client.exception;

import java.util.UUID;

public class AccountBlockedException extends RuntimeException {
  public AccountBlockedException(UUID accountId) {
    super(String.format("Account with ID %s is blocked and cannot process transactions.", accountId.toString()));
  }
}

