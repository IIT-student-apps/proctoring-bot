package org.bsuir.proctoringbot.service;

import org.bsuir.proctoringbot.bot.security.UserDetails;

import java.util.List;

public interface SubjectService {
    List<List<String>> getAllSubjects(UserDetails userDetails);

    List<List<String>> getAllLinks(UserDetails userDetails);
    List<List<String>> getAllLectures(UserDetails userDetails);

    List<List<String>> getAllLabWorks(UserDetails userDetails);

    List<List<String>> getAllLinksForTeacher(UserDetails userDetails);
    List<List<String>> getAllLecturesForTeacher(UserDetails userDetails);

    List<List<String>> getAllLabWorksForTeacher(UserDetails userDetails);

    List<String> getAllLabWorksNames(UserDetails userDetails);

    void addSubject(String subjectRequest, UserDetails userDetails);
}
