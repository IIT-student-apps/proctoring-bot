package org.bsuir.proctoringbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsuir.proctoringbot.bot.exception.TelegramMessageException;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.model.IntermediateState;
import org.bsuir.proctoringbot.model.LabWork;
import org.bsuir.proctoringbot.model.SimpleTelegramUser;
import org.bsuir.proctoringbot.repository.LabWorkRepository;
import org.bsuir.proctoringbot.util.SpreadsheetsUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LabWorkService {

    private static final int LAB_WORK_INFO_COLUMN_SIZE = 5;
    private static final int COMPLETED_LAB_WORK_SHIFT_INDEX = -3;
    private static final int STUDENTS_START_POSITION_SHIFT = 3;

    private final IntermediateStateService intermediateStateService;
    private final GoogleDriveService googleDriveService;
    private final LabWorkRepository labWorkRepository;
    private final SpreadsheetsService spreadsheetsService;

    public void saveLabWork(UserDetails user, String link) {
        if (!googleDriveService.isAccessibleLink(link)) {
            throw new TelegramMessageException("Ссылка неккоректная или нет доступа для просмотра");
        }
        IntermediateState intermediateState = intermediateStateService.getIntermediateState(user);
        String labWorkNumber = intermediateState.getState().getPickedLabWorkNumber();
        String group = spreadsheetsService.getStudentGroup(user);
        String subject = intermediateState.getState().getPickedSubject();
        if (labWorkNumber == null || subject == null) {
            log.warn("user {} tried to save lab work without subject or number", user.getName());
            throw new TelegramMessageException("Вы не можете скинуть лабораторную работу(");
        }
        saveLabWorkToSheet(subject, group, labWorkNumber, user.getName(), link);
        saveLabWorkToDb((SimpleTelegramUser) user, link, subject, labWorkNumber);
    }

    private void saveLabWorkToDb(SimpleTelegramUser user, String link, String subject, String labWorkNumber) {
        LabWork labWork = LabWork.builder()
                .user(user)
                .subject(subject)
                .labNumber(labWorkNumber)
                .link(link)
                .build();

        labWorkRepository.save(labWork);
    }


    private void saveLabWorkToSheet(String subject, String group, String labWorkNumber, String studentName, String link) {
        String spreadsheetURL = spreadsheetsService.getSubjectSpreadsheetURL(subject, group);
        String spreadsheetId = SpreadsheetsUtil.getSpreadsheetId(spreadsheetURL);
        List<List<String>> allLabs = spreadsheetsService.getAllLabs(spreadsheetId);
        Integer labWorkIndex = null;
        for (int i = 0; i < allLabs.size(); i++) {
            List<String> lab = allLabs.get(i);
            if (!lab.isEmpty() && labWorkNumber.equalsIgnoreCase(lab.get(0))) {
                labWorkIndex = i;
                break;
            }
        }

        if (labWorkIndex == null) {
            throw new TelegramMessageException("Нет такой лабораторной работы");
        }

        String columnLetter = SpreadsheetsUtil.getColumnLetter(LAB_WORK_INFO_COLUMN_SIZE *
                (labWorkIndex + 1) + COMPLETED_LAB_WORK_SHIFT_INDEX + 1);
        int studentPositionFromSubjectSheet = spreadsheetsService.getStudentPositionFromSubjectSheet(spreadsheetId,
                group,
                studentName) +
                STUDENTS_START_POSITION_SHIFT;
        spreadsheetsService.writeToCell(spreadsheetId, group, columnLetter, studentPositionFromSubjectSheet, link);
    }


    public List<LabWork> getAllLabWorks(UserDetails user){
        IntermediateState intermediateState = intermediateStateService.getIntermediateState(user);
        List<LabWork> labWorks = labWorkRepository.getAllAddedLabWorks(intermediateState.getState().getPickedSubject());
        return labWorks;
    }


}
