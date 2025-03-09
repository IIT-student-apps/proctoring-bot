package org.bsuir.proctoringbot.repository;

import org.bsuir.proctoringbot.model.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TestRepository extends JpaRepository<Test, Integer> {

    List<Test> findAllByAuthorId(Long authorId);

    boolean existsByName(String name);

    @Query("SELECT t FROM Test t WHERE t.startTime = :startTime")
    Iterable<Test> findAllByStartTime(@Param("startTime")LocalDateTime startTime);
}
