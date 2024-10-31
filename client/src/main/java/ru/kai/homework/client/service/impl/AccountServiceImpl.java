package ru.kai.homework.client.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kai.homework.client.exception.AccountNotFoundException;
import ru.kai.homework.client.mapper.AccountMapper;
import ru.kai.homework.client.model.Account;
import ru.kai.homework.client.model.dto.request.AccountRequest;
import ru.kai.homework.client.model.enums.AccountType;
import ru.kai.homework.client.repository.AccountRepository;
import ru.kai.homework.client.service.AccountService;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Value("${t1.credit-limit}")
    private BigDecimal defaultCreditLimit;

    @Transactional
    public Account registerAccount(AccountRequest request) {
        Account account  = accountMapper.toEntity(request);
        if(account.getType() == AccountType.CREDIT) {
            account.setBalance(defaultCreditLimit);
        }
        accountRepository.save(account);
        return account;
    }

    @Transactional
    public void blockAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.setBlocked(true);
    }

    public Account getById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

}
