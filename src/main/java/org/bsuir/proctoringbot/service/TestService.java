package org.bsuir.proctoringbot.service;

import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.model.Test;

import java.util.List;

public interface TestService {

    List<Test> getAllUnactivatedTests(UserDetails userDetails);

    void addTest(UserDetails userDetails, String message);

    void activateTest(UserDetails userDetails, String testName);

}
