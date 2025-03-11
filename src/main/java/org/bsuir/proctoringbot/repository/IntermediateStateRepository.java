package org.bsuir.proctoringbot.repository;

import org.bsuir.proctoringbot.model.IntermediateState;
import org.bsuir.proctoringbot.model.SimpleTelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IntermediateStateRepository extends JpaRepository<IntermediateState, Integer> {

    @Query("select s from IntermediateState s where s.user.id = :userId")
    Optional<IntermediateState> findIntermediateStateByUserId(Long userId);

    long user(SimpleTelegramUser user);
}
