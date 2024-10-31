package ru.kai.homework.client.mapper;

import org.mapstruct.*;
import ru.kai.homework.client.model.Client;
import ru.kai.homework.client.model.dto.request.ClientRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    Client toEntity(ClientRequest clientRequest);
}
