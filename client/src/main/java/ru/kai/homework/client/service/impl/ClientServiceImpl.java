package ru.kai.homework.client.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kai.homework.client.mapper.ClientMapper;
import ru.kai.homework.client.model.Client;
import ru.kai.homework.client.model.dto.request.ClientRequest;
import ru.kai.homework.client.repository.ClientRepository;
import ru.kai.homework.client.service.ClientService;

@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional
    public Client registerClient(ClientRequest request) {
        Client client = clientMapper.toEntity(request);
        clientRepository.save(client);
        return client;
    }
}
