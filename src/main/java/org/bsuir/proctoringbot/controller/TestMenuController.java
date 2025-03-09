package org.bsuir.proctoringbot.controller;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramController;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequest;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequestMapping;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramResponse;
import org.bsuir.proctoringbot.bot.exception.TelegramMessageException;
import org.bsuir.proctoringbot.bot.security.AllowedRoles;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.bot.security.UserService;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.bsuir.proctoringbot.service.TestService;
import org.bsuir.proctoringbot.util.TelegramUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static org.bsuir.proctoringbot.model.Constants.ADD_TEST_INFO_BUTTON_CALLBACK;

@TelegramController
@RequiredArgsConstructor
public class TestMenuController {

    private final UserService dbUserService;
    private final TestService testService;

    @TelegramRequestMapping(from = State.PICK_TESTS_MENU_ITEM)
    @AllowedRoles(Role.TEACHER)
    public void menu(TelegramRequest req, TelegramResponse resp) {
        if (req.getUpdate().hasCallbackQuery()) {
            CallbackQuery callbackQuery = req.getUpdate().getCallbackQuery();
            String callbackData = callbackQuery.getData();
            UserDetails user = req.getUser();
            switch (callbackData) {
                case ADD_TEST_INFO_BUTTON_CALLBACK -> {
                    user.setState(State.TEACHER_ADD_TEST);
                    resp.setResponse(SendMessage.builder()
                            .chatId(TelegramUtil.getChatId(req.getUpdate()))
                            .text("Введите название теста, ссылку, номер группы и время начала теста(необязательно)")
                            .build());
                    user.setState(State.TEACHER_ADD_TEST);
                }
                default -> throw new TelegramMessageException("Для такой кнопки нет функционала");
            }
            dbUserService.updateUser(user);
        } else {
            UserDetails user = req.getUser();
            user.setState(State.PICK_TESTS_MENU_ITEM);
            dbUserService.updateUser(user);
        }
    }

    @TelegramRequestMapping(from = State.TEACHER_ADD_TEST, to = State.MENU_TEACHER)
    @AllowedRoles(Role.TEACHER)
    public void addTest(TelegramRequest req, TelegramResponse resp) {
        testService.addTest(req.getUser(), req.getMessage());
        resp.setResponse(SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text("Тест успешно добавлен")
                .build());
    }

}
