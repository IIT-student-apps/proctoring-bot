package org.bsuir.proctoringbot.service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.bsuir.proctoringbot.model.SimpleTelegramUser;
import org.bsuir.proctoringbot.util.SpreadsheetsUtil;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class SpreadsheetsService {

    @Value("${google.sheets.table-id}")
    private String teachersSheetId;
    @Value("${google.sheets.table-id}")
    private String studentsSheetId;
    private final Sheets sheets;

    public Optional<UserDetails> findTeacherByUsername(String username) {
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

        } catch (Exception e) {
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

    public void writeToCell(String spreadsheetId, String listName, String column, int row, String content){
        try {
            String range = String.format("%s!%s%d", listName, column, row);
            ValueRange body = new ValueRange()
                    .setValues(List.of(
                            Collections.singletonList(content)
                    ));

            sheets.spreadsheets().values()
                    .update(spreadsheetId, range, body)
                    .setValueInputOption("RAW")
                    .execute();

            log.info("Content '{}' successfully written to range '{}'", content, range);
        } catch (Exception e) {
            log.error("Failed to write to Google Sheets", e);
            throw new RuntimeException("Не получилось записать в таблицу");
        }
    }

    public int getStudentPositionFromSubjectSheet(String spreadsheetId, String group, String name){
        String range = "A3:A";
        List<List<Object>> lists = readWithOffset(spreadsheetId, group, range);
        for (int i = 0; i < lists.size(); i++){
            if (lists.get(i).isEmpty()) continue;
            if (name.equalsIgnoreCase(lists.get(i).get(0).toString())){
                return i;
            }
        }
        throw new IllegalArgumentException("Нет такого студента в группе");
    }

    public void addSubjectInfo(String link, String listName, List<String> info){
        List<Object> rowAsObjects = new ArrayList<>(info);
        addToEnd(link, listName, "A2", "D", rowAsObjects);
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

    public String getSubjectSpreadsheetURL(String subject, String group) {
        String listName = "Subjects";
        String startCell = "A2";
        String endColumn = "C";
        int filterSubjectColumnPosition = 0;
        int filterGroupColumnPosition = 1;
        int urlColumnPosition = 2;
        List<List<String>> rowsByFilter = findRowsByFilter(listName,
                startCell,
                endColumn,
                List.of(filterSubjectColumnPosition, filterGroupColumnPosition),
                List.of(subject, group));
        for (List<String> strings : rowsByFilter) {
            if (strings.size() >= urlColumnPosition + 1) {
                return strings.get(urlColumnPosition);
            }
        }
        return "";
    }

    public String getSubjectSpreadsheetURLForTeacher(String subject, String teacherUsername) {
        String listName = "Subjects";
        String startCell = "A2";
        String endColumn = "D";
        int filterSubjectColumnPosition = 0;
        int filterTeacherUsernameColumnPosition = 3;
        int urlColumnPosition = 2;
        List<List<String>> rowsByFilter = findRowsByFilter(listName,
                startCell,
                endColumn,
                List.of(filterSubjectColumnPosition, filterTeacherUsernameColumnPosition),
                List.of(subject, teacherUsername));
        for (List<String> strings : rowsByFilter) {
            if (strings.size() >= urlColumnPosition + 1) {
                return strings.get(urlColumnPosition);
            }
        }
        return "";
    }


    public void addNewSubject(List<List<String>> subjects) {
        for (List<String> row : subjects) {
            List<Object> rowAsObjects = new ArrayList<>(row);
            boolean isUpdated = update("Subjects", "A2", "D", rowAsObjects, List.of(0, 1, 3));
            if (!isUpdated) {
                addToEnd(studentsSheetId,"Subjects", "A2", "D", rowAsObjects);
            }
        }
    }


    public int isSheetPublic(String spreadsheetUrl) {
        try {
            String spreadsheetId = SpreadsheetsUtil.getSpreadsheetId(spreadsheetUrl);
            String range = "A1";
            sheets.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            if (!SpreadsheetsUtil.isSpreadsheetOpenForRead(spreadsheetUrl)) {
                return 3;
            }
            return 0;
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == 403) {
                return 1;
            }
            if (e.getStatusCode() == 404) {
                return 2;
            }
            throw new RuntimeException("Ошибка при проверке доступа к таблице: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Неизвестная ошибка: " + e.getMessage(), e);
        }
    }

    private boolean addToEnd(String link,
                             String listName,
                             String startCell,
                             String endColumn,
                             List<Object> row
    ) {
        String startColumn = startCell.replaceAll("\\d", "");
        int startRow = Integer.parseInt(startCell.replaceAll("\\D", ""));
        String range = listName + "!" + startCell + ":" + endColumn;
        try {
            ValueRange response = sheets.spreadsheets().values()
                    .get(link, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null) {
                startRow = 2;
            } else {
                startRow = values.size() + 2;
            }
            String cell = listName + "!" + startColumn + startRow;
            ValueRange body = new ValueRange().setValues(Collections.singletonList(row));
            sheets.spreadsheets().values()
                    .update(link, cell, body)
                    .setValueInputOption("RAW")
                    .execute();
            System.out.println("Запись добавлена в конец.");
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении данных", e);
        }
    }


    private boolean update(String listName,
                           String startCell,
                           String endColumn,
                           List<Object> row,
                           List<Integer> indexesToCompare
    ) {
        String startColumn = startCell.replaceAll("\\d", "");
        int startRow = Integer.parseInt(startCell.replaceAll("\\D", ""));
        String range = listName + "!" + startCell + ":" + endColumn;
        try {
            ValueRange response = sheets.spreadsheets().values()
                    .get(studentsSheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                System.out.println("Нет данных для обновления.");
                return false;
            }
            for (int i = 0; i < values.size(); i++) {
                List<Object> currentRow = values.get(i);
                boolean match = true;
                for (int index : indexesToCompare) {
                    if (!currentRow.get(index).toString().trim().equals(row.get(index).toString().trim())) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    String cell = listName + "!" + startColumn + (i + startRow);
                    ValueRange body = new ValueRange().setValues(Collections.singletonList(row));
                    sheets.spreadsheets().values()
                            .update(studentsSheetId, cell, body)
                            .setValueInputOption("RAW")
                            .execute();
                    System.out.println("Запись обновлена.");
                    return true;
                }
            }
            System.out.println("Запись не найдена для обновления.");
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении данных", e);
        }
    }

    public List<Long> getStudentsTgIdsByGroup(String group) {
        String listName = "Students";
        String startCell = "A2";
        String endColumn = "C";
        int filterColumnPosition = 1;
        int studentIdColumnPosition = 2;
        List<List<String>> rowsByFilter = findRowsByFilter(listName,
                startCell,
                endColumn,
                List.of(filterColumnPosition),
                List.of(group));

        List<Long> studentsIds = new ArrayList<>();

        for (List<String> row : rowsByFilter) {
            if (row.size() < 3) {
                continue;
            }
            try {
                String id = row.get(studentIdColumnPosition);
                studentsIds.add(Long.parseLong(id));
            } catch (NumberFormatException e) {
                log.warn("wrong student id in list {}: {}", listName, e.getMessage());
            }
        }
        return studentsIds;
    }

    public List<String> getTeachersGroups(UserDetails userDetails) {
        String listName = "Subjects";
        String startCell = "A2";
        String endColumn = "D";
        int filterColumnPosition = 3;
        int groupColumnPosition = 1;
        List<List<String>> rowsByFilter = findRowsByFilter(listName,
                startCell,
                endColumn,
                List.of(filterColumnPosition),
                List.of(userDetails.getUsername()));
        List<String> teachersGroups = new ArrayList<>();
        for (List<String> strings : rowsByFilter) {
            teachersGroups.add(strings.get(groupColumnPosition));
        }
        return teachersGroups;
    }

    public List<String> getTeacherSubjects(UserDetails userDetails) {
        String listName = "Subjects";
        String startCell = "A2";
        String endColumn = "D";
        int filterColumnPosition = 3;
        int groupColumnPosition = 0;
        List<List<String>> rowsByFilter = findRowsByFilter(listName,
                startCell,
                endColumn,
                List.of(filterColumnPosition),
                List.of(userDetails.getUsername()));
        List<String> teachersSubjects = new ArrayList<>();
        for (List<String> strings : rowsByFilter) {
            teachersSubjects.add(strings.get(groupColumnPosition));
        }
        return teachersSubjects;
    }

    public List<List<String>> getAllLinks(String spreadsheetId) {
        String listName = "'Полезные ссылки'";
        String range = "A1:B";

        return getSubjectInfo(spreadsheetId, listName, range);
    }

    public List<List<String>> getAllLectures(String spreadsheetId) {
        String listName = "Лекции";
        String range = "A1:B";

        return getSubjectInfo(spreadsheetId, listName, range);
    }

    public List<List<String>> getAllLabs(String spreadsheetId) {
        String listName = "Лабораторные";
        String range = "A2:B";

        return getSubjectInfo(spreadsheetId, listName, range);
    }

    private List<List<String>> getSubjectInfo(String spreadsheetId, String listName, String range) {
        List<List<String>> result = new ArrayList<>();

        List<List<Object>> lists = readWithOffset(spreadsheetId, listName, range);
        for (List<Object> list : lists) {
            if (list.size() < 2) continue;
            List<String> link = new ArrayList<>();
            link.add(list.get(0).toString());
            link.add(list.get(1).toString());
            result.add(link);
        }
        return result;
    }

    public List<String> getLabWorksNames(String spreadsheetId) {
        String listName = "Лабораторные";
        List<String> result = new ArrayList<>();

        try {
            String range = listName + "!A2:A";

            ValueRange response = sheets.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();

            if (values == null || values.isEmpty()) {
                return result;
            }

            for (List<Object> row : values) {
                if (!row.isEmpty()) {
                    result.add(row.get(0).toString());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return result;
    }

    private List<List<String>> findRowsByFilter(String listName,
                                                String startCell,
                                                String endColumn,
                                                List<Integer> filterColumnsPositions,
                                                List<String> filterParams) {
        String range = listName + "!" + startCell + ":" + endColumn;
        List<List<String>> results = new ArrayList<>();
        if (filterColumnsPositions.size() != filterParams.size()) {
            throw new RuntimeException("number of positions and params are not equal");
        }
        try {
            ValueRange response = sheets.spreadsheets().values()
                    .get(studentsSheetId, range)
                    .execute();

            List<List<Object>> rows = response.getValues();
            int maxColumnPosition = filterColumnsPositions.stream().max(Integer::compareTo).orElse(0);
            for (List<Object> row : rows) {
                if (row.size() < maxColumnPosition) continue;
                boolean isAgreedByFilter = true;
                for (Integer columnPosition : filterColumnsPositions) {
                    if (!row.get(columnPosition).equals(filterParams.get(filterColumnsPositions.indexOf(columnPosition)))) {
                        isAgreedByFilter = false;
                        break;
                    }
                }
                if (isAgreedByFilter) {
                    results.add(row.stream().map(Object::toString).toList());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return results;
    }

    private List<List<Object>> readWithOffset(String listName,
                                              String startCell,
                                              String endColumn,
                                              int offset,
                                              int limit) {
        String startColumn = startCell.replaceAll("\\d", ""); // A
        int startRow = Integer.parseInt(startCell.replaceAll("\\D", "")); // 2
        int start = startRow + offset;
        int end = start + limit - 1;
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

    private List<List<Object>> readWithOffset(String sheetId, String sheetName, String range) {
        try {
            ValueRange response = sheets.spreadsheets().values()
                    .get(sheetId, String.format("%s!%s", sheetName, range))
                    .execute();
            return Optional.ofNullable(response.getValues()).orElse(Collections.emptyList());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении данных", e);
        }
    }


}
