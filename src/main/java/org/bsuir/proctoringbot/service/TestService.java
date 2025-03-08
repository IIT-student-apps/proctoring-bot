package org.bsuir.proctoringbot.service;

import org.bsuir.proctoringbot.bot.security.UserDetails;

public interface TestService {
    String getAllTests(UserDetails userDetails);
}
