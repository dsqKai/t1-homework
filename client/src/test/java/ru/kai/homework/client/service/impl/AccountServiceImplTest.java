package ru.kai.homework.client.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import ru.kai.homework.client.exception.AccountNotFoundException;
import ru.kai.homework.client.mapper.AccountMapper;
import ru.kai.homework.client.model.Account;
import ru.kai.homework.client.model.dto.request.AccountRequest;
import ru.kai.homework.client.model.enums.AccountType;
import ru.kai.homework.client.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Value("${t1.credit-limit}")
    private BigDecimal defaultCreditLimit = BigDecimal.valueOf(1000.00);

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl(accountRepository, accountMapper);
        accountService.defaultCreditLimit = defaultCreditLimit;
    }

    @Test
    void testRegisterAccountWithCreditType() {
        AccountRequest request = AccountRequest.builder()
                .clientId(UUID.randomUUID())
                .type(AccountType.CREDIT)
                .build();

        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setType(AccountType.CREDIT);

        when(accountMapper.toEntity(any(AccountRequest.class))).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = accountService.registerAccount(request);

        assertNotNull(result);
        assertEquals(AccountType.CREDIT, result.getType());
        assertEquals(defaultCreditLimit, result.getBalance());
        verify(accountMapper, times(1)).toEntity(request);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testRegisterAccountWithDebitType() {
        AccountRequest request = AccountRequest.builder()
                .clientId(UUID.randomUUID())
                .type(AccountType.DEBT)
                .build();

        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setType(AccountType.DEBT);

        when(accountMapper.toEntity(any(AccountRequest.class))).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = accountService.registerAccount(request);

        assertNotNull(result);
        assertEquals(AccountType.DEBT, result.getType());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        verify(accountMapper, times(1)).toEntity(request);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testBlockAccount() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setId(accountId);
        account.setBlocked(false);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.blockAccount(accountId);

        assertTrue(account.isBlocked());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void testBlockAccountThrowsExceptionWhenAccountNotFound() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> accountService.blockAccount(accountId));
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void testGetById() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Account result = accountService.getById(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void testGetByIdThrowsExceptionWhenAccountNotFound() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getById(accountId));
        verify(accountRepository, times(1)).findById(accountId);
    }
}
