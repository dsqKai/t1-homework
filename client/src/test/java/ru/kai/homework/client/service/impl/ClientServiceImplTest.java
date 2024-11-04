package ru.kai.homework.client.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kai.homework.client.mapper.ClientMapper;
import ru.kai.homework.client.model.Client;
import ru.kai.homework.client.model.dto.request.ClientRequest;
import ru.kai.homework.client.repository.ClientRepository;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void testRegisterClient() {
        ClientRequest request = new ClientRequest();
        request.setFirstName("test");
        request.setLastName("test");

        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());

        when(clientMapper.toEntity(any(ClientRequest.class))).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Client result = clientService.registerClient(request);

        assertEquals(client.getFirstName(), result.getFirstName());
        assertEquals(client.getLastName(), result.getLastName());
        verify(clientMapper, times(1)).toEntity(request);
        verify(clientRepository, times(1)).save(client);
    }
}
