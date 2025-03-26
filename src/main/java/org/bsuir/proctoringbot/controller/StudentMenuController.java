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
import org.bsuir.proctoringbot.model.IntermediateState;
import org.bsuir.proctoringbot.model.IntermediateStateData;
import org.bsuir.proctoringbot.service.IntermediateStateService;
import org.bsuir.proctoringbot.service.SubjectService;
import org.bsuir.proctoringbot.service.TestService;
import org.bsuir.proctoringbot.transformer.SubjectTransformer;
import org.bsuir.proctoringbot.util.TelegramUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.bsuir.proctoringbot.util.Constants.*;

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

    private final UserService dbUserService;

    private final TestService testService;

    private final IntermediateStateService intermediateStateService;

    private final SubjectService subjectService;

    private final SubjectTransformer subjectTransformer;


    @TelegramRequestMapping(from = State.MENU_STUDENT, to = State.PICK_STUDENT_MENU_ITEM)
    @AllowedRoles(Role.STUDENT)
    public void menu(TelegramRequest req, TelegramResponse resp) {

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
    public void pickMenuItem(TelegramRequest req, TelegramResponse resp) {
        if (req.getUpdate().hasCallbackQuery()) {
            CallbackQuery callbackQuery = req.getUpdate().getCallbackQuery();
            String callbackData = callbackQuery.getData();
            UserDetails user = req.getUser();
            switch (callbackData) {
                case GET_INFO_STUDENT_MENU_BUTTON_CALLBACK -> {
                    user.setState(State.PICK_SUBJECT_STUDENT);
                    List<List<String>> subjects = subjectService.getAllSubjects(user);
                    createResponseGetAllSubjects(req, resp, subjects);
                }
                case SEND_WORK_STUDENT_MENU_BUTTON_CALLBACK -> {
                    user.setState(State.PICK_SUBJECT_TO_SEND_WORK_STUDENT);
                    List<List<String>> subjects = subjectService.getAllSubjects(user);
                    createResponseGetAllSubjects(req, resp, subjects);
                }
                case GET_RESULTS_STUDENT_MENU_BUTTON_CALLBACK -> subjectService.getAllSubjects(user);
                case TAKE_TEST_STUDENT_MENU_BUTTON_CALLBACK -> testService.getAllUnactivatedTests(user);
                default -> throw new TelegramMessageException("Для такой кнопки нет функционала");
            }
            dbUserService.updateUser(user);
        } else {
            UserDetails user = req.getUser();
            user.setState(State.MENU_STUDENT);
            dbUserService.updateUser(user);
        }
    }

    @TelegramRequestMapping(from = State.PICK_SUBJECT_TO_SEND_WORK_STUDENT, to = State.PICK_LAB_WORK_STUDENT)
    @AllowedRoles(Role.STUDENT)
    public void pickSubjectToSendWork(TelegramRequest req, TelegramResponse resp) {
        if (req.getUpdate().hasCallbackQuery()) {
            CallbackQuery callbackQuery = req.getUpdate().getCallbackQuery();
            String callbackData = callbackQuery.getData();
            UserDetails user = req.getUser();
            intermediateStateService.updateIntermediateState(user, IntermediateStateData.builder()
                    .pickedSubject(callbackData)
                    .build());
            List<String> subjects = subjectService.getAllLabWorks(user);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            for (String subject : subjects) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(subject);
                inlineKeyboardButton.setCallbackData(subject);
                rowsInline.add(List.of(inlineKeyboardButton));
            }
            inlineKeyboardMarkup.setKeyboard(rowsInline);
            resp.setResponse(SendMessage.builder()
                    .text("Выберите лабораторную:")
                    .chatId(TelegramUtil.getChatId(req.getUpdate()))
                    .replyMarkup(inlineKeyboardMarkup)
                    .build());
        } else {
            resp.setResponse(SendMessage.builder()
                    .chatId(TelegramUtil.getChatId(req.getUpdate()))
                    .text("выберите предмет, нажав кнопку")
                    .build()
            );
        }
    }

    @TelegramRequestMapping(from = State.PICK_LAB_WORK_STUDENT, to = State.STUDENT_SHARE_LAB_WORK_LINK)
    @AllowedRoles(Role.STUDENT)
    public void pickLabWorkNumber(TelegramRequest req, TelegramResponse resp) {
        if (req.getUpdate().hasCallbackQuery()) {
            CallbackQuery callbackQuery = req.getUpdate().getCallbackQuery();
            String callbackData = callbackQuery.getData();
            UserDetails user = req.getUser();
            intermediateStateService.updateIntermediateState(
                    user,
                    IntermediateStateData.builder()
                    .pickedLabWorkNumber(callbackData)
                    .build()
            );
            resp.setResponse(SendMessage.builder()
                    .chatId(TelegramUtil.getChatId(req.getUpdate()))
                    .text("скиньте ссылку на лабораторную работу:")
                    .build());
        } else {
            resp.setResponse(SendMessage.builder()
                    .chatId(TelegramUtil.getChatId(req.getUpdate()))
                    .text("выберите лабораторную, нажав кнопку")
                    .build()
            );
        }
    }

    @TelegramRequestMapping(from = State.STUDENT_SHARE_LAB_WORK_LINK, to = State.MENU_STUDENT)
    @AllowedRoles(Role.STUDENT)
    public void sendLabWork(TelegramRequest req, TelegramResponse resp) {
        if (req.getUpdate().hasMessage()) {
            String link = req.getUpdate().getMessage().getText();

            resp.setResponse(SendMessage.builder()
                    .chatId(TelegramUtil.getChatId(req.getUpdate()))
                    .text("скиньте ссылку на лабораторную работу:")
                    .build());
        } else {
            resp.setResponse(SendMessage.builder()
                    .chatId(TelegramUtil.getChatId(req.getUpdate()))
                    .text("выберите лабораторную, нажав кнопку")
                    .build()
            );
        }
    }

    private void createResponseGetAllSubjects(TelegramRequest req, TelegramResponse resp, List<List<String>> subjects) {
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text(subjectTransformer.transformForGetAllSubjects(subjects))
                .build();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (List<String> subject : subjects) {
            String subjectName = subject.get(0);
            InlineKeyboardButton button = new InlineKeyboardButton(subjectName);
            button.setCallbackData(subjectName);
            rowsInline.add(List.of(button));
        }
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        message.setReplyMarkup(inlineKeyboardMarkup);
        resp.setResponse(message);
    }


}
