package org.bsuir.proctoringbot.controller;


import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramController;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequest;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequestMapping;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramResponse;
import org.bsuir.proctoringbot.bot.security.AllowedRoles;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    @TelegramRequestMapping(from = State.MENU_TEACHER, to = State.PICK_TEACHER_MENU_ITEM)
    @AllowedRoles(Role.TEACHER)
    public void menu(TelegramRequest req, TelegramResponse resp){

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
}
