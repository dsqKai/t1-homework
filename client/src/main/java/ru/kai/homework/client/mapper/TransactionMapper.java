package ru.kai.homework.client.mapper;

import org.mapstruct.*;
import ru.kai.homework.client.model.Transaction;
import ru.kai.homework.client.model.dto.request.TransactionRequest;
import ru.kai.homework.client.model.enums.TransactionStatus;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
    @Mapping(target = "status", constant = "WAITING")
    Transaction toEntity(TransactionRequest dto);
    TransactionRequest toDto(Transaction transaction);

    default TransactionStatus waitingStatus() {
        return TransactionStatus.WAITING;
    }
}