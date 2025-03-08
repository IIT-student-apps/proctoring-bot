package org.bsuir.proctoringbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.service.TestService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    @Override
    public String getAllTests(UserDetails userDetails) {
        return "";
    }
}
