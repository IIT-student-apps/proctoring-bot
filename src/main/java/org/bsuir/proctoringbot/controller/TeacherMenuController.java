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
import org.bsuir.proctoringbot.model.IntermediateStateData;
import org.bsuir.proctoringbot.service.IntermediateStateService;
import org.bsuir.proctoringbot.service.SpreadsheetsService;
import org.bsuir.proctoringbot.service.TeacherService;
import org.bsuir.proctoringbot.util.TelegramUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.bsuir.proctoringbot.bot.statemachine.State.*;
import static org.bsuir.proctoringbot.util.Constants.*;

@TelegramController
@RequiredArgsConstructor
public class TeacherMenuController {

    private static final Map<String, String> FIRST_ROW_BUTTONS = Map.of(
            ADD_SUBJECT_BUTTON_MESSAGE, ADD_SUBJECT_BUTTON_CALLBACK,
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

    private static final Map<String, String> SUBJECT_FIRST_MENU_BUTTONS = Map.of(
            GET_WORKS_INFO, GET_WORKS_INFO_CALLBACK
    );

    private static final Map<String, String> SUBJECT_SECOND_MENU_BUTTONS = Map.of(
            GET_MATERIALS_INFO, GET_MATERIALS_INFO_CALLBACK
    );

    private static final Map<String, String> SUBJECT_THIRD_MENU_BUTTONS = Map.of(
            GET_LECTURES_INFO, GET_LECTURES_INFO_CALLBACK
    );

    private static final Map<String, String> SUBJECT_FORTH_MENU_BUTTONS = Map.of(
            ADD_LECTURES_INFO, ADD_LECTURES_INFO_CALLBACK
    );

    private static final Map<String, String> SUBJECT_FIFTH_MENU_BUTTONS = Map.of(
            ADD_MATERIALS_INFO, ADD_MATERIALS_INFO_CALLBACK
    );

    private static final Map<String, String> SUBJECT_SIXTH_MENU_BUTTONS = Map.of(
            ADD_WORKS_INFO, ADD_WORKS_INFO_CALLBACK
    );


    private static final Map<String, String> TEST_MENU_BUTTONS_SECOND_ROW = Map.of(
            DELETE_TEST_INFO_BUTTON, DELETE_TEST_INFO_BUTTON_CALLBACK,
            ACTIVATE_TEST_BUTTON, ACTIVATE_TEST_BUTTON_CALLBACK
    );

    private final UserService dbUserService;

    private final SpreadsheetsService spreadsheetsService;

    private final IntermediateStateService intermediateStateService;

    private final TeacherService teacherService;

    @TelegramRequestMapping(from = State.MENU_TEACHER, to = State.PICK_TEACHER_MENU_ITEM)
    @AllowedRoles(Role.TEACHER)
    public void startMenu(TelegramRequest req, TelegramResponse resp) {

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

        List<InlineKeyboardButton> thirdRowButtons = new ArrayList<>();
        THIRD_ROW_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            thirdRowButtons.add(button);
        });

        rowsInline.add(firstButtons);
        rowsInline.add(secondRowButtons);
        rowsInline.add(thirdRowButtons);
        inlineKeyboardMarkup.setKeyboard(rowsInline);

        message.setReplyMarkup(inlineKeyboardMarkup);

        resp.setResponse(message);

    }

    @TelegramRequestMapping(from = State.PICK_TEACHER_MENU_ITEM)
    @AllowedRoles(Role.TEACHER)
    public void pickStartMenuItem(TelegramRequest req, TelegramResponse resp) {
        if (req.getUpdate().hasCallbackQuery()) {
            CallbackQuery callbackQuery = req.getUpdate().getCallbackQuery();
            String callbackData = callbackQuery.getData();
            UserDetails user = req.getUser();
            switch (callbackData) {
                case ADD_SUBJECT_BUTTON_CALLBACK -> {
                    createResponseAddSubject(req, resp);
                    user.setState(State.ADD_NEW_SUBJECT);
                }
                case WORK_WITH_TESTS_BUTTON_CALLBACK -> {
                    user.setState(State.PICK_TESTS_MENU_ITEM);
                    createTestMenuResponse(req, resp);
                }
                case UPDATE_SUBJECT_INFO_BUTTON_CALLBACK -> {
                    user.setState(State.GET_TEACHER_SUBJECTS);
                    createGetAllSubjectsResponse(req, resp);
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


    @TelegramRequestMapping(from = State.SUBJECT_TEACHER_UPDATE_MENU)
    @AllowedRoles(Role.TEACHER)
    public void pickSubjectTeacherMenuItem(TelegramRequest req, TelegramResponse resp) {
        if (req.getUpdate().hasCallbackQuery()) {
            CallbackQuery callbackQuery = req.getUpdate().getCallbackQuery();
            String callbackData = callbackQuery.getData();
            UserDetails user = req.getUser();
            switch (callbackData) {
                case ADD_WORKS_INFO_CALLBACK -> {
                    createResponseAddSubjectInfo(req, resp, "введите название и ссылку на лабораторную");
                    user.setState(ADD_LAB_WORK_TEACHER_MENU);
                }
                case ADD_LECTURES_INFO_CALLBACK -> {
                    user.setState(State.ADD_LECTURE_TEACHER_MENU);
                    createResponseAddSubjectInfo(req, resp, "введите название и ссылку на лекции");
                }
                case ADD_MATERIALS_INFO_CALLBACK -> {
                    user.setState(State.ADD_MATERIALS_TEACHER_MENU);
                    createResponseAddSubjectInfo(req, resp, "введите название и ссылку на материалы");
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

    private void createResponseAddSubjectInfo(TelegramRequest req, TelegramResponse resp, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text(text)
                .build();
        resp.setResponse(message);
    }

    @TelegramRequestMapping(from = ADD_LAB_WORK_TEACHER_MENU, to = MENU_TEACHER)
    public void pickAddLabWorkItem(TelegramRequest req, TelegramResponse resp) {
        if (!req.getUpdate().hasMessage()) {
            throw new TelegramMessageException("введите название и ссылку на лабораторную");
        }
        String text = req.getUpdate().getMessage().getText();
        teacherService.addInfoToSubject(req.getUser(), text, "Лабораторные");
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text("информация успешно сохранена")
                .build();
        resp.setResponse(message);
    }

    @TelegramRequestMapping(from = ADD_LECTURE_TEACHER_MENU, to = MENU_TEACHER)
    public void pickAddLectureItem(TelegramRequest req, TelegramResponse resp) {
        if (!req.getUpdate().hasMessage()) {
            throw new TelegramMessageException("введите название и ссылку на лекцию");
        }
        String text = req.getUpdate().getMessage().getText();
        teacherService.addInfoToSubject(req.getUser(), text, "Лекции");
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text("информация успешно сохранена")
                .build();
        resp.setResponse(message);
    }

    @TelegramRequestMapping(from = ADD_MATERIALS_TEACHER_MENU, to = MENU_TEACHER)
    public void pickAddMaterialsItem(TelegramRequest req, TelegramResponse resp) {
        if (!req.getUpdate().hasMessage()) {
            throw new TelegramMessageException("введите название и ссылку на материалы");
        }
        String text = req.getUpdate().getMessage().getText();
        teacherService.addInfoToSubject(req.getUser(), text, "Полезные ссылки");
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text("информация успешно сохранена")
                .build();
        resp.setResponse(message);
    }

    @TelegramRequestMapping(from = State.GET_TEACHER_SUBJECTS, to = State.SUBJECT_TEACHER_UPDATE_MENU)
    @AllowedRoles(Role.TEACHER)
    public void subjectMenu(TelegramRequest req, TelegramResponse resp) {
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text("Меню предметов:")
                .build();
        String subject = req.getUpdate().getCallbackQuery().getData();
        intermediateStateService.updateIntermediateState(req.getUser(),
                IntermediateStateData.builder()
                        .pickedSubject(subject)
                        .build());
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> firstRowButtons = new ArrayList<>();
        List<InlineKeyboardButton> secondRowButtons = new ArrayList<>();
        List<InlineKeyboardButton> thirdRowButtons = new ArrayList<>();
        List<InlineKeyboardButton> forthRowButtons = new ArrayList<>();
        List<InlineKeyboardButton> fifthRowButtons = new ArrayList<>();
        List<InlineKeyboardButton> sixthRowButtons = new ArrayList<>();
        SUBJECT_FIRST_MENU_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            firstRowButtons.add(button);
        });
        SUBJECT_SECOND_MENU_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            secondRowButtons.add(button);
        });
        SUBJECT_THIRD_MENU_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            thirdRowButtons.add(button);
        });
        SUBJECT_FORTH_MENU_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            forthRowButtons.add(button);
        });
        SUBJECT_FIFTH_MENU_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            fifthRowButtons.add(button);
        });
        SUBJECT_SIXTH_MENU_BUTTONS.forEach((buttonText, buttonCallback) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(buttonCallback);
            sixthRowButtons.add(button);
        });

        rowsInline.add(firstRowButtons);
        rowsInline.add(secondRowButtons);
        rowsInline.add(thirdRowButtons);
        rowsInline.add(forthRowButtons);
        rowsInline.add(fifthRowButtons);
        rowsInline.add(sixthRowButtons);
        keyboard.setKeyboard(rowsInline);
        message.setReplyMarkup(keyboard);
        resp.setResponse(message);
    }

    private void createGetAllSubjectsResponse(TelegramRequest req, TelegramResponse resp) {

        List<String> teacherSubjects = spreadsheetsService.getTeacherSubjects(req.getUser());
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        teacherSubjects.forEach((subject) -> {
            InlineKeyboardButton button = new InlineKeyboardButton(subject);
            button.setCallbackData(subject);
            buttons.add(button);
        });

        buttons.forEach((button) -> rowsInline.add(List.of(button)));
        inlineKeyboardMarkup.setKeyboard(rowsInline);

        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text("Выберете предмет:")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        resp.setResponse(message);
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

    private void createResponseAddSubject(TelegramRequest req, TelegramResponse resp) {
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text("Введите название предмета, ссылку на гугл таблицу и группы, у которых будете вести предмет\n" +
                        "Пример: ОМИС, https://docs.google.com/spreadsheets/d/1Cr, 221701, 221702, 221703")
                .build();
        resp.setResponse(message);
    }

}
