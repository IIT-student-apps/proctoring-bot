package org.bsuir.proctoringbot.repository;

import org.bsuir.proctoringbot.model.LabWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LabWorkRepository extends JpaRepository<LabWork, Long> {

    @Query("select l from LabWork l join fetch l.user where l.status = 'ADDED' and l.subject = :subject")
    List<LabWork> getAllAddedLabWorks(String subject);
}
