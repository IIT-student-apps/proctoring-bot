package org.bsuir.proctoringbot.controller;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramController;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequest;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequestMapping;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramResponse;
import org.bsuir.proctoringbot.bot.security.AllowedRoles;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.bsuir.proctoringbot.service.SubjectService;
import org.bsuir.proctoringbot.validator.InputValidator;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@TelegramController
@RequiredArgsConstructor
public class SubjectController {

    private final InputValidator inputValidator;

    private final SubjectService subjectService;

    @TelegramRequestMapping(from = State.ADD_NEW_SUBJECT, to = State.PICK_TEACHER_MENU_ITEM)
    @AllowedRoles(Role.TEACHER)
    public void addNewSubject(TelegramRequest req, TelegramResponse resp) {
        String subjectRequest = req.getMessage();
        inputValidator.validateNewSubjectInput(subjectRequest);
        subjectService.addSubject(subjectRequest, req.getUser());
        SendMessage message = MenuControllerHelper.getTeacherMenuSendMessageWithText("Предмет успешно сохранен\nМеню:", req);
        resp.setResponse(message);
    }

}
