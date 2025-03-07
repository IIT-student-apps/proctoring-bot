package org.bsuir.proctoringbot.service;

import org.bsuir.proctoringbot.bot.security.UserDetails;

public interface SubjectService {
    String getAllSubjects(UserDetails userDetails);

    String getAllWorksType(UserDetails userDetails);
}
