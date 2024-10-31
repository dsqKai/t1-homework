package ru.kai.homework.correction.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kai.homework.correction.model.Correction;
import ru.kai.homework.correction.model.CorrectionStatus;

import java.util.UUID;

@Repository
public interface CorrectionRepository extends JpaRepository<Correction, UUID> {
    Page<Correction> findByStatus(CorrectionStatus correctionStatus, Pageable pageable);
}
