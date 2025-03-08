package org.bsuir.proctoringbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.service.SpreadsheetsService;
import org.bsuir.proctoringbot.service.SubjectService;
import org.bsuir.proctoringbot.transformer.SubjectTransformer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SpreadsheetsService spreadsheetsService;

    private final SubjectTransformer subjectTransformer;

    @Override
    public List<List<String>> getAllSubjects(UserDetails userDetails) {
        String studentGroup = spreadsheetsService.getStudentGroup(userDetails);
        return spreadsheetsService.getAllSubjectsByGroup(studentGroup);
    }

    @Override
    public String getAllWorksType(UserDetails userDetails) {
        return "";
    }

    @Override
    public void addSubject(String subjectRequest, UserDetails userDetails) {
        List<List<String>> subjects = subjectTransformer.transformForAddSubject(subjectRequest, userDetails);
        spreadsheetsService.addNewSubject(subjects);
    }
}
