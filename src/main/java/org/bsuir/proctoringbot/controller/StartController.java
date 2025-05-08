package org.bsuir.proctoringbot.controller;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramController;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequest;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequestMapping;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramResponse;
import org.bsuir.proctoringbot.bot.security.AllowedRoles;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.bsuir.proctoringbot.service.StudentService;
import org.bsuir.proctoringbot.util.TelegramUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@TelegramController
@RequiredArgsConstructor
public class StartController {

    public final StudentService studentService;

    @TelegramRequestMapping(from = State.NEW_TEACHER, to = State.PICK_TEACHER_MENU_ITEM)
    @AllowedRoles(Role.TEACHER)
    public void helloTeacher(TelegramRequest req, TelegramResponse resp){
        SendMessage message = MenuControllerHelper.getTeacherMenuSendMessageWithText("Добро пожаловать, " + req.getUser().getName() + "!\n" + "Меню:", req);

        resp.setResponse(message);
    }

    @TelegramRequestMapping(from = State.NEW, to = State.REGISTRATION)
    @AllowedRoles(Role.USER)
    public void helloUser(TelegramRequest req, TelegramResponse resp){
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text("Чтобы продолжить как студент введите 'ФИО и номер группы'")
                .build();

        resp.setResponse(message);
    }

    @TelegramRequestMapping(from = State.REGISTRATION, to = State.PICK_STUDENT_MENU_ITEM)
    @AllowedRoles(Role.USER)
    public void registerStudent(TelegramRequest req, TelegramResponse resp){
        studentService.registerUserByNameAndGroup(req.getUser(), req.getMessage());
        SendMessage message = MenuControllerHelper.getStudentMenuSendMessageWithText("Вы успешно зарегистрированы как студент\nМеню:", req);
        resp.setResponse(message);
    }
}
