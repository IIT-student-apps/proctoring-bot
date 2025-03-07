package org.bsuir.proctoringbot.controller;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramController;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequest;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequestMapping;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramResponse;
import org.bsuir.proctoringbot.bot.security.AllowedRoles;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.bsuir.proctoringbot.service.SubjectService;
import org.bsuir.proctoringbot.service.TestService;
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
public class StudentMenuController {
    private static final Map<String, String> FIRST_ROW_BUTTONS = Map.of(
            GET_INFO_STUDENT_MENU_BUTTON_MESSAGE, GET_INFO_STUDENT_MENU_BUTTON_CALLBACK,
            SEND_WORK_STUDENT_MENU_BUTTON_MESSAGE, SEND_WORK_STUDENT_MENU_BUTTON_CALLBACK
    );

    private static final Map<String, String> SECOND_ROW_BUTTONS = Map.of(
            GET_RESULTS_STUDENT_MENU_BUTTON_MESSAGE, GET_RESULTS_STUDENT_MENU_BUTTON_CALLBACK,
            TAKE_TEST_STUDENT_MENU_BUTTON_MESSAGE, TAKE_TEST_STUDENT_MENU_BUTTON_CALLBACK
    );

    private final TestService testService;

    private final SubjectService subjectService;


    @TelegramRequestMapping(from = State.MENU_STUDENT, to = State.PICK_STUDENT_MENU_ITEM)
    @AllowedRoles(Role.STUDENT)
    public void menu(TelegramRequest req, TelegramResponse resp){

        SendMessage message = SendMessage.builder()
                .chatId(req.getUpdate().getMessage().getFrom().getId())
                .text("Меню:")
                .build();

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

        rowsInline.add(firstButtons);
        rowsInline.add(secondRowButtons);
        inlineKeyboardMarkup.setKeyboard(rowsInline);

        message.setText("Меню:");
        message.setReplyMarkup(inlineKeyboardMarkup);
        
        resp.setResponse(message);
    }

    @TelegramRequestMapping(from = State.PICK_STUDENT_MENU_ITEM)
    @AllowedRoles(Role.STUDENT)
    public void pickMenuItem(TelegramRequest req, TelegramResponse resp){
        if (req.getUpdate().hasCallbackQuery()){
            CallbackQuery callbackQuery = req.getUpdate().getCallbackQuery();
            String callbackData = callbackQuery.getData();
            UserDetails user = req.getUser();
            String responseText = switch (callbackData) {
                case GET_INFO_STUDENT_MENU_BUTTON_CALLBACK -> subjectService.getAllSubjects(user);
                case SEND_WORK_STUDENT_MENU_BUTTON_CALLBACK -> subjectService.getAllWorksType(user);
                case GET_RESULTS_STUDENT_MENU_BUTTON_CALLBACK -> subjectService.getAllSubjects(user);
                case TAKE_TEST_STUDENT_MENU_BUTTON_CALLBACK -> testService.getAllTests(user);
                default -> "Неизвестная команда!";
            };
            SendMessage message = SendMessage.builder()
                    .chatId(req.getUpdate().getMessage().getFrom().getId())
                    .text(responseText)
                    .build();
            resp.setResponse(message);
        }
    }
}
