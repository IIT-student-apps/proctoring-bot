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
import org.bsuir.proctoringbot.model.Test;
import org.bsuir.proctoringbot.service.TestService;
import org.bsuir.proctoringbot.util.TelegramUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.bsuir.proctoringbot.util.Constants.ACTIVATE_TEST_BUTTON_CALLBACK;
import static org.bsuir.proctoringbot.util.Constants.ADD_TEST_INFO_BUTTON_CALLBACK;

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
                            .text("Введите название теста, ссылку, ссылку на таблицу с результатами, номер группы и время начала теста(необязательно)")
                            .build());
                    user.setState(State.TEACHER_ADD_TEST);
                }
                case ACTIVATE_TEST_BUTTON_CALLBACK -> {
                    user.setState(State.TEACHER_ACTIVATE_TEST);
                    formActivateTestRequest(req, resp);
                    user.setState(State.TEACHER_ACTIVATE_TEST);
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

    @TelegramRequestMapping(from = State.TEACHER_ADD_TEST, to = State.PICK_TEACHER_MENU_ITEM)
    @AllowedRoles(Role.TEACHER)
    public void addTest(TelegramRequest req, TelegramResponse resp) {
        testService.addTest(req.getUser(), req.getMessage());
        SendMessage message = MenuControllerHelper.getTeacherMenuSendMessageWithText("Тест успешно добавлен", req);
        resp.setResponse(message);
    }

    @TelegramRequestMapping(from = State.TEACHER_ACTIVATE_TEST, to = State.PICK_TEACHER_MENU_ITEM)
    @AllowedRoles(Role.TEACHER)
    public void activateTest(TelegramRequest req, TelegramResponse resp) {
        if (req.getUpdate().getCallbackQuery() == null){
            throw new TelegramMessageException("Пожалуйста, выберите тест через кнопку");
        }
        testService.activateTest(req.getUser(), req.getUpdate().getCallbackQuery().getData());
        SendMessage message = MenuControllerHelper.getTeacherMenuSendMessageWithText("Тест успешно активирован", req);
        resp.setResponse(message);
    }

    private void formActivateTestRequest(TelegramRequest req, TelegramResponse resp){
        List<Test> tests = testService.getAllUnactivatedTests(req.getUser());
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text("Выберите тест для активации:")
                .build();

        List<InlineKeyboardButton> buttons = new ArrayList<>();

        for (Test test : tests) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(test.getName())
                    .callbackData(test.getName())
                    .build();
            buttons.add(button);
        }

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboard(Collections.singletonList(buttons))
                .build();

        message.setReplyMarkup(keyboard);

        resp.setResponse(message);
    }

}
