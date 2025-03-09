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
import org.bsuir.proctoringbot.service.impl.TestServiceImpl;
import org.bsuir.proctoringbot.util.TelegramUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.bsuir.proctoringbot.model.Constants.*;

@TelegramController
@RequiredArgsConstructor
public class TeacherMenuController {

    private static final Map<String, String> FIRST_ROW_BUTTONS = Map.of(
            UPDATE_SUBJECT_INFO_BUTTON_MESSAGE, UPDATE_SUBJECT_INFO_BUTTON_CALLBACK
    );

    private static final Map<String, String> SECOND_ROW_BUTTONS = Map.of(
            EDIT_LAB_WORK_BUTTON_MESSAGE, EDIT_LAB_WORK_BUTTON_CALLBACK
    );

    private static final Map<String, String> THIRD_ROW_BUTTONS = Map.of(
            WORK_WITH_TESTS_BUTTON_MESSAGE, WORK_WITH_TESTS_BUTTON_CALLBACK
    );

    private static final Map<String, String> TEST_MENU_BUTTONS_FIRST_ROW = Map.of(
            ADD_TEST_INFO_BUTTON, ADD_TEST_INFO_BUTTON_CALLBACK,
            EDIT_TEST_INFO_BUTTON, EDIT_TEST_INFO_BUTTON_CALLBACK
    );

    private static final Map<String, String> TEST_MENU_BUTTONS_SECOND_ROW = Map.of(
            DELETE_TEST_INFO_BUTTON, DELETE_TEST_INFO_BUTTON_CALLBACK,
            ACTIVATE_TEST_BUTTON, ACTIVATE_TEST_BUTTON_CALLBACK
    );

    private final UserService dbUserService;
    private final TestService testServiceImpl;

    @TelegramRequestMapping(from = State.MENU_TEACHER, to = State.PICK_TEACHER_MENU_ITEM)
    @AllowedRoles(Role.TEACHER)
    public void menu(TelegramRequest req, TelegramResponse resp) {

        SendMessage message = SendMessage.builder()
                .chatId(req.getUpdate().getMessage().getFrom().getId())
                .text("Добро пожаловать!!!")
                .build();

        //панелька
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> firstButtons = new ArrayList<>();
        FIRST_ROW_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            firstButtons.add(button);
        });

        List<InlineKeyboardButton> secondRowButtons = new ArrayList<>();
        SECOND_ROW_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            secondRowButtons.add(button);
        });

        List<InlineKeyboardButton> thirdRowButtons = new ArrayList<>();
        THIRD_ROW_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            secondRowButtons.add(button);
        });

        rowsInline.add(firstButtons);
        rowsInline.add(secondRowButtons);
        rowsInline.add(thirdRowButtons);
        inlineKeyboardMarkup.setKeyboard(rowsInline);

        message.setText("Меню:");
        message.setReplyMarkup(inlineKeyboardMarkup);

        resp.setResponse(message);

    }

    @TelegramRequestMapping(from = State.PICK_TEACHER_MENU_ITEM)
    @AllowedRoles(Role.TEACHER)
    public void pickMenuItem(TelegramRequest req, TelegramResponse resp) {
        if (req.getUpdate().hasCallbackQuery()) {
            CallbackQuery callbackQuery = req.getUpdate().getCallbackQuery();
            String callbackData = callbackQuery.getData();
            UserDetails user = req.getUser();
            switch (callbackData) {
                case WORK_WITH_TESTS_BUTTON_CALLBACK -> {
                    user.setState(State.PICK_TESTS_MENU_ITEM);
                    createTestMenuResponse(req, resp);
                }
                default -> throw new TelegramMessageException("Для такой кнопки нет функционала");
            }
            dbUserService.updateUser(user);
        } else {
            UserDetails user = req.getUser();
            user.setState(State.MENU_TEACHER);
            dbUserService.updateUser(user);
        }
    }

    private void createTestMenuResponse(TelegramRequest req, TelegramResponse resp) {
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text("Меню тестов:")
                .build();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> firstLine = new ArrayList<>();
        List<InlineKeyboardButton> secondLine = new ArrayList<>();

        TEST_MENU_BUTTONS_FIRST_ROW.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            firstLine.add(button);
        });

        TEST_MENU_BUTTONS_SECOND_ROW.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            secondLine.add(button);
        });

        rowsInline.add(firstLine);
        rowsInline.add(secondLine);

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        message.setReplyMarkup(inlineKeyboardMarkup);
        resp.setResponse(message);
    }

}
