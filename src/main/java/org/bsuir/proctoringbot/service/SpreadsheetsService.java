package org.bsuir.proctoringbot.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.bsuir.proctoringbot.model.SimpleTelegramUser;
import org.springframework.beans.factory.annotation.Value;

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

}
