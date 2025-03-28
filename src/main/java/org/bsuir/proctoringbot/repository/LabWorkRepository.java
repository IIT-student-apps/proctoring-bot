package org.bsuir.proctoringbot.repository;

import org.bsuir.proctoringbot.model.LabWork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabWorkRepository extends JpaRepository<LabWork, Long> {
}
