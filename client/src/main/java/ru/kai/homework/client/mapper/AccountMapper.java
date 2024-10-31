package ru.kai.homework.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.kai.homework.client.model.Account;
import ru.kai.homework.client.model.dto.request.AccountRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(source = "clientId", target = "client.id")
    Account toEntity(AccountRequest accountRequest);
}