package org.bsuir.proctoringbot.repository;

import org.bsuir.proctoringbot.model.SimpleTelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SimpleTelegramUser, Long> {

    Optional<SimpleTelegramUser> findByUsername(String username);

}
