package org.bsuir.proctoringbot.repository;

import org.bsuir.proctoringbot.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Integer> {

    List<Test> findAllByAuthorIdAndStartTimeIsNull(Long authorId);

    Optional<Test> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT t FROM Test t WHERE t.startTime = :startTime")
    Iterable<Test> findAllByStartTime(@Param("startTime")LocalDateTime startTime);
}
