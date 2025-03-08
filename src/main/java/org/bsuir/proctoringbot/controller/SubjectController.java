package org.bsuir.proctoringbot.controller;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramController;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequest;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequestMapping;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramResponse;
import org.bsuir.proctoringbot.bot.security.AllowedRoles;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.bsuir.proctoringbot.validator.InputValidator;

@TelegramController
@RequiredArgsConstructor
public class SubjectController {

    private final InputValidator inputValidator;

    @TelegramRequestMapping(from = State.ADD_NEW_SUBJECT, to = State.MENU_TEACHER)
    @AllowedRoles(Role.TEACHER)
    public void addNewSubject(TelegramRequest req, TelegramResponse resp) {
        System.out.println("addNewSubject");
        inputValidator.validateNewSubjectInput(req.getMessage());
    }
}
