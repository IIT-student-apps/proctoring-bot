package org.bsuir.proctoringbot.controller;

import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequest;
import org.bsuir.proctoringbot.util.TelegramUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.bsuir.proctoringbot.util.Constants.ADD_SUBJECT_BUTTON_CALLBACK;
import static org.bsuir.proctoringbot.util.Constants.ADD_SUBJECT_BUTTON_MESSAGE;
import static org.bsuir.proctoringbot.util.Constants.CHECK_LAB_WORK_BUTTON_CALLBACK;
import static org.bsuir.proctoringbot.util.Constants.EDIT_LAB_WORK_BUTTON_CALLBACK;
import static org.bsuir.proctoringbot.util.Constants.CHECK_LAB_WORK_BUTTON_MESSAGE;
import static org.bsuir.proctoringbot.util.Constants.GET_INFO_STUDENT_MENU_BUTTON_CALLBACK;
import static org.bsuir.proctoringbot.util.Constants.GET_INFO_STUDENT_MENU_BUTTON_MESSAGE;
import static org.bsuir.proctoringbot.util.Constants.GET_RESULTS_STUDENT_MENU_BUTTON_CALLBACK;
import static org.bsuir.proctoringbot.util.Constants.GET_RESULTS_STUDENT_MENU_BUTTON_MESSAGE;
import static org.bsuir.proctoringbot.util.Constants.SEND_WORK_STUDENT_MENU_BUTTON_CALLBACK;
import static org.bsuir.proctoringbot.util.Constants.SEND_WORK_STUDENT_MENU_BUTTON_MESSAGE;
import static org.bsuir.proctoringbot.util.Constants.TAKE_TEST_STUDENT_MENU_BUTTON_CALLBACK;
import static org.bsuir.proctoringbot.util.Constants.TAKE_TEST_STUDENT_MENU_BUTTON_MESSAGE;
import static org.bsuir.proctoringbot.util.Constants.UPDATE_SUBJECT_INFO_BUTTON_CALLBACK;
import static org.bsuir.proctoringbot.util.Constants.UPDATE_SUBJECT_INFO_BUTTON_MESSAGE;
import static org.bsuir.proctoringbot.util.Constants.WORK_WITH_TESTS_BUTTON_CALLBACK;
import static org.bsuir.proctoringbot.util.Constants.WORK_WITH_TESTS_BUTTON_MESSAGE;

public class MenuControllerHelper {

    private static final Map<String, String> FIRST_ROW_TEACHER_BUTTONS = Map.of(
            ADD_SUBJECT_BUTTON_MESSAGE, ADD_SUBJECT_BUTTON_CALLBACK,
            UPDATE_SUBJECT_INFO_BUTTON_MESSAGE, UPDATE_SUBJECT_INFO_BUTTON_CALLBACK
    );

    private static final Map<String, String> SECOND_ROW_TEACHER_BUTTONS = Map.of(
            CHECK_LAB_WORK_BUTTON_MESSAGE, CHECK_LAB_WORK_BUTTON_CALLBACK
    );

    private static final Map<String, String> THIRD_ROW_TEACHER_BUTTONS = Map.of(
            WORK_WITH_TESTS_BUTTON_MESSAGE, WORK_WITH_TESTS_BUTTON_CALLBACK
    );


    private static final Map<String, String> FIRST_ROW_STUDENT_BUTTONS = Map.of(
            GET_INFO_STUDENT_MENU_BUTTON_MESSAGE, GET_INFO_STUDENT_MENU_BUTTON_CALLBACK,
            SEND_WORK_STUDENT_MENU_BUTTON_MESSAGE, SEND_WORK_STUDENT_MENU_BUTTON_CALLBACK
    );

    private static final Map<String, String> SECOND_ROW_STUDENT_BUTTONS = Map.of(
            GET_RESULTS_STUDENT_MENU_BUTTON_MESSAGE, GET_RESULTS_STUDENT_MENU_BUTTON_CALLBACK,
            TAKE_TEST_STUDENT_MENU_BUTTON_MESSAGE, TAKE_TEST_STUDENT_MENU_BUTTON_CALLBACK
    );

    public static SendMessage getStudentMenuSendMessageWithText(String textMessage, TelegramRequest req){
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text(textMessage)
                .build();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> firstButtons = new ArrayList<>();
        FIRST_ROW_STUDENT_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            firstButtons.add(button);
        });

        List<InlineKeyboardButton> secondRowButtons = new ArrayList<>();
        SECOND_ROW_STUDENT_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            secondRowButtons.add(button);
        });

        rowsInline.add(firstButtons);
        rowsInline.add(secondRowButtons);
        inlineKeyboardMarkup.setKeyboard(rowsInline);

        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }

    public static SendMessage getTeacherMenuSendMessageWithText(String textMessage, TelegramRequest req) {
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text(textMessage)
                .build();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> firstButtons = new ArrayList<>();
        FIRST_ROW_TEACHER_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            firstButtons.add(button);
        });

        List<InlineKeyboardButton> secondRowButtons = new ArrayList<>();
        SECOND_ROW_TEACHER_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            secondRowButtons.add(button);
        });

        List<InlineKeyboardButton> thirdRowButtons = new ArrayList<>();
        THIRD_ROW_TEACHER_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            thirdRowButtons.add(button);
        });

        rowsInline.add(firstButtons);
        rowsInline.add(secondRowButtons);
        rowsInline.add(thirdRowButtons);
        inlineKeyboardMarkup.setKeyboard(rowsInline);

        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }

}
