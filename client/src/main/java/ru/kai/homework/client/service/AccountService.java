package ru.kai.homework.client.service;

import ru.kai.homework.client.model.Account;
import ru.kai.homework.client.model.dto.request.AccountRequest;

import java.util.UUID;

public interface AccountService {
    Account registerAccount(AccountRequest request);

    void blockAccount(UUID accountId);

    Account getById(UUID accountId);

}
