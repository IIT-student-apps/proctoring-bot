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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@TelegramController
@RequiredArgsConstructor
public class StartController {

    public final StudentService studentService;

    @TelegramRequestMapping(from = State.NEW_TEACHER, to = State.MENU_TEACHER)
    @AllowedRoles(Role.TEACHER)
    public void helloTeacher(TelegramRequest req, TelegramResponse resp){
        SendMessage message = SendMessage.builder()
                .chatId(req.getUpdate().getMessage().getFrom().getId())
                .text("Добро пожаловать, " + req.getUser().getName() + "!")
                .build();

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Кнопка 1");
        row1.add("Кнопка 2");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Кнопка 3");


        keyboard.add(row1);
        keyboard.add(row2);
        keyboardMarkup.setKeyboard(keyboard);

        message.setReplyMarkup(keyboardMarkup);

        resp.setResponse(message);
    }

    @TelegramRequestMapping(from = State.NEW, to = State.REGISTRATION)
    @AllowedRoles(Role.USER)
    public void helloUser(TelegramRequest req, TelegramResponse resp){
        SendMessage message = SendMessage.builder()
                .chatId(req.getUpdate().getMessage().getFrom().getId())
                .text("Чтобы продолжить как студент введите 'ФИО и номер группы'")
                .build();

        resp.setResponse(message);
    }

    @TelegramRequestMapping(from = State.REGISTRATION, to = State.MENU_STUDENT)
    @AllowedRoles(Role.USER)
    public void registerStudent(TelegramRequest req, TelegramResponse resp){
        studentService.registerUserByNameAndGroup(req.getUser(), req.getMessage());
        resp.setResponse(SendMessage.builder()
                .chatId(req.getUpdate().getMessage().getFrom().getId())
                .text("Вы успешно зарегистрированы как студент")
                .build());
    }

}
