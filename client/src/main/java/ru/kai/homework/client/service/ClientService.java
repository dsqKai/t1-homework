package ru.kai.homework.client.service;

import ru.kai.homework.client.model.Client;
import ru.kai.homework.client.model.dto.request.ClientRequest;

public interface ClientService {
    Client registerClient(ClientRequest request);
}
