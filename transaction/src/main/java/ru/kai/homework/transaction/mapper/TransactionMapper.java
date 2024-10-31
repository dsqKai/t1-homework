package ru.kai.homework.transaction.mapper;

import org.mapstruct.*;
import ru.kai.homework.transaction.model.Transaction;
import ru.kai.homework.transaction.model.dto.TransactionDto;
import ru.kai.homework.transaction.model.dto.request.TransactionRequest;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    Transaction toEntity(TransactionRequest transactionRequest);
    TransactionDto toDto(Transaction transaction);
}