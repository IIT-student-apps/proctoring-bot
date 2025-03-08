package org.bsuir.proctoringbot.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.bsuir.proctoringbot.model.SimpleTelegramUser;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class SpreadsheetsService {

    @Value("${google.sheets.table-id}")
    private String teachersSheetId;
    @Value("${google.sheets.table-id}")
    private String studentsSheetId;
    private final Sheets sheets;

    public Optional<UserDetails> findTeacherByUsername(String username){
        String range = "Teachers!A2:B";

        try {

            ValueRange response = sheets.spreadsheets().values()
                    .get(teachersSheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                return Optional.empty();
            }

            for (List<Object> row : values) {
                if (row.size() >= 2 && username.equals(row.get(1).toString().trim())) {
                    SimpleTelegramUser teacher = SimpleTelegramUser.builder()
                            .name(row.get(0).toString())
                            .username(row.get(1).toString())
                            .role(Role.TEACHER)
                            .state(State.NEW_TEACHER)
                            .build();
                    return Optional.of(teacher);
                }
            }

        } catch (Exception e){
            return Optional.empty();
        }

        return Optional.empty();
    }

    public List<List<String>> getStudents() {
        String range = "Students!A2:C";

        try {
            ValueRange response = sheets.spreadsheets().values()
                    .get(studentsSheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                return Collections.emptyList();
            }

            return values.stream()
                    .map(row -> row.stream().map(Object::toString).toList())
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка студентов", e);
        }
    }

    public void updateTelegramIdForStudentByNameAndGroup(String name, String group, String telegramId) {
        String range = "Students!A2:C";

        try {
            ValueRange response = sheets.spreadsheets().values()
                    .get(studentsSheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                throw new RuntimeException("Список студентов пуст");
            }

            for (int i = 0; i < values.size(); i++) {
                List<Object> row = values.get(i);
                if (row.size() >= 2 && name.equals(row.get(0).toString().trim()) && group.equals(row.get(1).toString().trim())) {
                    String cell = "Students!C" + (i + 2);
                    ValueRange body = new ValueRange().setValues(Collections.singletonList(Collections.singletonList(telegramId)));
                    sheets.spreadsheets().values()
                            .update(studentsSheetId, cell, body)
                            .setValueInputOption("RAW")
                            .execute();
                    return;
                }
            }

            throw new RuntimeException("Студент не найден в таблице");

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении Telegram ID", e);
        }
    }

    public String getStudentGroup(UserDetails userDetails) {
        int offset = 0;
        int limit = 100;
        List<List<Object>> values;
        do {
            values = readWithOffset("Students", "A2", "C", offset, limit);
            offset += limit;
            for (List<Object> row : values) {
                if (!row.isEmpty() && row.size() < 3) {
                    continue;
                }
                if (row.get(2).toString().equals(userDetails.getId().toString())) {
                    return row.size() > 1 ? row.get(1).toString() : null;
                }
            }
        } while (!values.isEmpty());
        return null;
    }

    public List<List<String>> getAllSubjectsByGroup(String group) {
        int offset = 0;
        int limit = 100;
        List<List<Object>> values;
        List<List<String>> result = new ArrayList<>();
        do {
            values = readWithOffset("Subjects", "A2", "C", offset, limit);
            offset += limit;
            for (List<Object> row : values) {
                if (!row.isEmpty() && row.get(1).toString().equals(group)) {
                    result.add(List.of(row.get(0).toString(), row.get(2).toString()));
                }
            }
        } while (!values.isEmpty());
        return result;
    }

    private List<List<Object>> readWithOffset(String listName,
                                              String startCell,
                                              String endColumn,
                                              int offset,
                                              int limit) {
        // Разбираем startCell (например, "A2" → столбец "A", строка 2)
        String startColumn = startCell.replaceAll("\\d", ""); // A
        int startRow = Integer.parseInt(startCell.replaceAll("\\D", "")); // 2

        // Начальная и конечная строки
        int start = startRow + offset;
        int end = start + limit - 1;

        // Формируем диапазон, например: "Students!A7:C16"
        String range = String.format("%s!%s%d:%s%d", listName, startColumn, start, endColumn, end);

        try {
            ValueRange response = sheets.spreadsheets().values()
                    .get(studentsSheetId, range)
                    .execute();

            return Optional.ofNullable(response.getValues()).orElse(Collections.emptyList());

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении данных с offset и limit", e);
        }
    }
}
