package org.bsuir.proctoringbot.service;

import org.bsuir.proctoringbot.bot.security.UserDetails;

import java.util.List;

public interface SubjectService {
    List<List<String>> getAllSubjects(UserDetails userDetails);

    List<String> getAllLabWorks(UserDetails userDetails);

    void addSubject(String subjectRequest, UserDetails userDetails);
}
