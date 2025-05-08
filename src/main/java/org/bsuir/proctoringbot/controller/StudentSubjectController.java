package org.bsuir.proctoringbot.controller;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramController;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequest;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequestMapping;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramResponse;
import org.bsuir.proctoringbot.bot.exception.TelegramMessageException;
import org.bsuir.proctoringbot.bot.security.AllowedRoles;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.security.UserService;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.bsuir.proctoringbot.model.IntermediateStateData;
import org.bsuir.proctoringbot.service.IntermediateStateService;
import org.bsuir.proctoringbot.service.SubjectService;
import org.bsuir.proctoringbot.util.TelegramUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.bsuir.proctoringbot.util.Constants.MENU_FOR_INFORMATION_ABOUT_LABS_BUTT0N;
import static org.bsuir.proctoringbot.util.Constants.MENU_FOR_INFORMATION_ABOUT_LABS_BUTT0N_CALLBACK;
import static org.bsuir.proctoringbot.util.Constants.MENU_FOR_LECTIONS_BUTT0N;
import static org.bsuir.proctoringbot.util.Constants.MENU_FOR_LECTIONS_BUTT0N_CALLBACK;
import static org.bsuir.proctoringbot.util.Constants.MENU_FOR_LINK_BUTT0N;
import static org.bsuir.proctoringbot.util.Constants.MENU_FOR_LINK_BUTT0N_CALLBACK;

@TelegramController
@RequiredArgsConstructor
public class StudentSubjectController {

    private static final Map<String, String> FIRST_ROW_BUTTONS = Map.of(
            MENU_FOR_LINK_BUTT0N, MENU_FOR_LINK_BUTT0N_CALLBACK
    );

    private static final Map<String, String> SECOND_ROW_BUTTONS = Map.of(
            MENU_FOR_INFORMATION_ABOUT_LABS_BUTT0N, MENU_FOR_INFORMATION_ABOUT_LABS_BUTT0N_CALLBACK
    );

    private static final Map<String, String> THIRD_ROW_BUTTONS = Map.of(
            MENU_FOR_LECTIONS_BUTT0N, MENU_FOR_LECTIONS_BUTT0N_CALLBACK
    );

    private final IntermediateStateService intermediateStateService;
    private final UserService dbUserService;
    private final SubjectService subjectService;

    @TelegramRequestMapping(from = State.MENU_STUDENT_GET_INFORMATION, to = State.PICK_STUDENT_MENU_ITEM)
    @AllowedRoles(Role.STUDENT)
    public void pickMenuAboutSubject(TelegramRequest req, TelegramResponse resp) {
        if (req.getUpdate().hasCallbackQuery()) {
            CallbackQuery callbackQuery = req.getUpdate().getCallbackQuery();
            String callbackData = callbackQuery.getData();
            switch (callbackData) {
                case MENU_FOR_LINK_BUTT0N_CALLBACK -> {
                    List<List<String>> links = subjectService.getAllLinks(req.getUser());
                    createResponseGetAllX(req, resp, links, "Ссылки: ");
                }
                case MENU_FOR_INFORMATION_ABOUT_LABS_BUTT0N_CALLBACK -> {
                    List<List<String>> labs = subjectService.getAllLabWorks(req.getUser());
                    createResponseGetAllX(req, resp, labs, "Лабораторные: ");
                }

                case MENU_FOR_LECTIONS_BUTT0N_CALLBACK -> {
                    List<List<String>> lectures = subjectService.getAllLectures(req.getUser());
                    createResponseGetAllX(req, resp, lectures, "Лекции: ");
                }
                default -> throw new TelegramMessageException("Для такой кнопки нет функционала");
            }

        } else {
            throw new TelegramMessageException("Выберите действие нажав по кнопке");
        }
    }

    private void createResponseGetAllX(TelegramRequest req, TelegramResponse resp, List<List<String>> links, String header) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s", header)).append(System.lineSeparator());
        for (int i = 0; i < links.size(); i++) {
            sb.append(i + 1).append(". ");
            for (String link : links.get(i)) {
                sb.append(link).append(" ");
            }
            sb.append(System.lineSeparator());
        }
        SendMessage message = MenuControllerHelper.getStudentMenuSendMessageWithText(sb + "\nМеню:", req);
        resp.setResponse(message);
    }

    @TelegramRequestMapping(from = State.PICK_SUBJECT_STUDENT, to = State.MENU_STUDENT_GET_INFORMATION)
    @AllowedRoles(Role.STUDENT)
    public void sendMenuGetInfoAboutLR(TelegramRequest req, TelegramResponse resp) {
        SendMessage message = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(req.getUpdate()))
                .text("Меню:")
                .build();

        String subject = req.getUpdate().getCallbackQuery().getData();
        intermediateStateService.updateIntermediateState(req.getUser(),
                IntermediateStateData.builder()
                        .pickedSubject(subject)
                        .build());

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

        message.setText("Меню:");
        message.setReplyMarkup(inlineKeyboardMarkup);

        resp.setResponse(message);
    }
}
