package org.bsuir.proctoringbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.exception.TelegramMessageException;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.model.IntermediateState;
import org.bsuir.proctoringbot.service.IntermediateStateService;
import org.bsuir.proctoringbot.service.SpreadsheetsService;
import org.bsuir.proctoringbot.service.SubjectService;
import org.bsuir.proctoringbot.transformer.SubjectTransformer;
import org.bsuir.proctoringbot.util.SpreadsheetsUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SpreadsheetsService spreadsheetsService;

    private final SubjectTransformer subjectTransformer;

    private final IntermediateStateService intermediateStateService;

    @Override
    public List<List<String>> getAllSubjects(UserDetails userDetails) {
        String studentGroup = spreadsheetsService.getStudentGroup(userDetails);
        return spreadsheetsService.getAllSubjectsByGroup(studentGroup);
    }

    @Override
    public List<String> getAllLabWorksNames(UserDetails userDetails) {
        String studentGroup = spreadsheetsService.getStudentGroup(userDetails);
        IntermediateState intermediateState = intermediateStateService.getIntermediateState(userDetails);
        String pickedSubject = intermediateState.getState().getPickedSubject();
        if (pickedSubject == null) {
            throw new TelegramMessageException("предмет не был выбран");
        }
        String spreadsheetId = SpreadsheetsUtil.getSpreadsheetId(
                spreadsheetsService.getSubjectSpreadsheetURL(pickedSubject, studentGroup)
        );
        return spreadsheetsService.getLabWorksNames(spreadsheetId);
    }

    @Override
    public void addSubject(String subjectRequest, UserDetails userDetails) {
        List<List<String>> subjects = subjectTransformer.transformForAddSubject(subjectRequest, userDetails);
        spreadsheetsService.addNewSubject(subjects);
    }

    @Override
    public List<List<String>> getAllLinks(UserDetails userDetails) {
        String subject = intermediateStateService.findIntermediateStateByUserId(userDetails.getId())
                .map(state -> state.getState().getPickedSubject())
                .orElseThrow(() -> new TelegramMessageException("Непредвиденная ошибка"));
        String studentGroup = spreadsheetsService.getStudentGroup(userDetails);
        String spreadsheetId = SpreadsheetsUtil.getSpreadsheetId(
                spreadsheetsService.getSubjectSpreadsheetURL(subject, studentGroup)
        );
        return spreadsheetsService.getAllLinks(spreadsheetId);
    }

    @Override
    public List<List<String>> getAllLectures(UserDetails userDetails) {
        String subject = intermediateStateService.findIntermediateStateByUserId(userDetails.getId())
                .map(state -> state.getState().getPickedSubject())
                .orElseThrow(() -> new TelegramMessageException("Непредвиденная ошибка"));
        String studentGroup = spreadsheetsService.getStudentGroup(userDetails);
        String spreadsheetId = SpreadsheetsUtil.getSpreadsheetId(
                spreadsheetsService.getSubjectSpreadsheetURL(subject, studentGroup)
        );
        return spreadsheetsService.getAllLectures(spreadsheetId);
    }

    @Override
    public List<List<String>> getAllLabWorks(UserDetails userDetails) {
        String subject = intermediateStateService.findIntermediateStateByUserId(userDetails.getId())
                .map(state -> state.getState().getPickedSubject())
                .orElseThrow(() -> new TelegramMessageException("Непредвиденная ошибка"));
        String studentGroup = spreadsheetsService.getStudentGroup(userDetails);
        String spreadsheetId = SpreadsheetsUtil.getSpreadsheetId(
                spreadsheetsService.getSubjectSpreadsheetURL(subject, studentGroup)
        );
        return spreadsheetsService.getAllLabs(spreadsheetId);
    }

    @Override
    public List<List<String>> getAllLinksForTeacher(UserDetails userDetails) {
        String subject = intermediateStateService.findIntermediateStateByUserId(userDetails.getId())
                .map(state -> state.getState().getPickedSubject())
                .orElseThrow(() -> new TelegramMessageException("Непредвиденная ошибка"));
        String studentGroup = spreadsheetsService.getStudentGroup(userDetails);
        List<String> teachersGroups = spreadsheetsService.getTeachersGroups(userDetails);
        String spreadsheetId = SpreadsheetsUtil.getSpreadsheetId(
                spreadsheetsService.getSubjectSpreadsheetURL(subject, teachersGroups.get(0))
        );
        return spreadsheetsService.getAllLinks(spreadsheetId);
    }

    @Override
    public List<List<String>> getAllLecturesForTeacher(UserDetails userDetails) {
        String subject = intermediateStateService.findIntermediateStateByUserId(userDetails.getId())
                .map(state -> state.getState().getPickedSubject())
                .orElseThrow(() -> new TelegramMessageException("Непредвиденная ошибка"));
        List<String> teachersGroups = spreadsheetsService.getTeachersGroups(userDetails);
        String spreadsheetId = SpreadsheetsUtil.getSpreadsheetId(
                spreadsheetsService.getSubjectSpreadsheetURL(subject, teachersGroups.get(0))
        );
        return spreadsheetsService.getAllLectures(spreadsheetId);
    }

    @Override
    public List<List<String>> getAllLabWorksForTeacher(UserDetails userDetails) {
        String subject = intermediateStateService.findIntermediateStateByUserId(userDetails.getId())
                .map(state -> state.getState().getPickedSubject())
                .orElseThrow(() -> new TelegramMessageException("Непредвиденная ошибка"));
        List<String> teachersGroups = spreadsheetsService.getTeachersGroups(userDetails);
        String spreadsheetId = SpreadsheetsUtil.getSpreadsheetId(
                spreadsheetsService.getSubjectSpreadsheetURL(subject, teachersGroups.get(0))
        );
        return spreadsheetsService.getAllLabs(spreadsheetId);
    }

}
