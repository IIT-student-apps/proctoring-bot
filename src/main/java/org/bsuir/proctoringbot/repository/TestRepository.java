package org.bsuir.proctoringbot.repository;

import org.bsuir.proctoringbot.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Integer> {

    List<Test> findAllByAuthorId(Long authorId);

    boolean existsByName(String name);

}
