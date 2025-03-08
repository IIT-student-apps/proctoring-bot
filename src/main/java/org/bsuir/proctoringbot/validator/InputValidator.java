package org.bsuir.proctoringbot.validator;

import org.bsuir.proctoringbot.bot.exception.TelegramMessageException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static org.bsuir.proctoringbot.util.Regex.*;

@Component
public class InputValidator {


    private static final Pattern SUBJECT_PATTERN = Pattern.compile(SUBJECT_REGEX);
    private static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX);
    private static final Pattern GROUPS_PATTERN = Pattern.compile(GROUPS_REGEX);

    public void validateNewSubjectInput(String input) {
        String[] parts = input.split("\\s*,\\s*", 3);

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
            throw new TelegramMessageException("Ошибка: Список групп должен содержать только числа из 6 цифр, разделенные запятыми.");
        }
    }
}
