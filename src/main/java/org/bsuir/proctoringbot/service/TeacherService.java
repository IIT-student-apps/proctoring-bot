package org.bsuir.proctoringbot.service;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.exception.TelegramMessageException;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.util.SpreadsheetsUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final SpreadsheetsService spreadsheetsService;
    private final GoogleDriveService googleDriveService;
    private final IntermediateStateService intermediateStateService;

    public void addInfoToSubject(UserDetails user, String message, String listName){
        String[] splitMessage = message.split("\\s");
        if (splitMessage.length != 2) {
            throw new TelegramMessageException("отправьте название и ссылку, разделенные пробелом");
        }
        if (!googleDriveService.isAccessibleLink(splitMessage[1])){
            throw new TelegramMessageException("ссылка недоступна для просмотра");
        }
        String pickedSubject = intermediateStateService.getIntermediateState(user).getState().getPickedSubject();
        String url = spreadsheetsService.getSubjectSpreadsheetURLForTeacher(pickedSubject, user.getUsername());
        spreadsheetsService.addSubjectInfo(SpreadsheetsUtil.getSpreadsheetId(url), listName, List.of(splitMessage[0], splitMessage[1]));
    }
}
