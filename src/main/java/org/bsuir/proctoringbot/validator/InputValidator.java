package org.bsuir.proctoringbot.validator;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.exception.TelegramMessageException;
import org.bsuir.proctoringbot.service.SpreadsheetsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static org.bsuir.proctoringbot.util.Regex.*;

@Component
@RequiredArgsConstructor
public class InputValidator {


    private static final Pattern SUBJECT_PATTERN = Pattern.compile(SUBJECT_REGEX);
    private static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX);
    private static final Pattern GROUPS_PATTERN = Pattern.compile(GROUPS_REGEX);

    private final SpreadsheetsService spreadsheetsService;

    @Value("${bot.email}")
    private String botEmail;

    public void validateNewSubjectInput(String input) {
        String[] parts = input.split(SEPARATOR_REGEX, 3);

        if (parts.length < 3) {
            throw new TelegramMessageException("Ошибка: Введены не все части. Ожидается <Название предмета>, <Ссылка>, <Список групп из 6 цифр>");
        }

        String subject = parts[0];
        String link = parts[1];
        String groups = parts[2];

        if (!SUBJECT_PATTERN.matcher(subject).matches()) {
            throw new TelegramMessageException("Ошибка: Некорректное название предмета.");
        }
        if (!LINK_PATTERN.matcher(link).matches()) {
            throw new TelegramMessageException("Ошибка: Некорректная ссылка на Google Таблицы.");
        }
        if (!GROUPS_PATTERN.matcher(groups).matches()) {
            throw new TelegramMessageException(
                    "Ошибка: Список групп должен содержать только числа из 6 цифр, разделенные запятыми."
            );
        }
        int statusCode = spreadsheetsService.isSheetPublic(link);
        if (statusCode == 1) {
            throw new TelegramMessageException(String.format(
                    "Откройте доступ к редактированию к таблице для данного email %s и повторите попытку!", botEmail
            ));
        } else if (statusCode == 2) {
            throw new TelegramMessageException("Такой таблицы не существует, повторите попытку!");
        }
        else if (statusCode == 3) {
            throw new TelegramMessageException("Откройте доступ для чтения для всех пользователей!");
        }
    }
}
