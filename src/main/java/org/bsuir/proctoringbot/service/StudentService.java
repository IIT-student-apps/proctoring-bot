package org.bsuir.proctoringbot.service;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.bot.security.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final SpreadsheetsService spreadsheetsService;
    private final UserService dbUserService;
    private static final Pattern STUDENT_REGISTRATION_MESSAGE_PATTERN = Pattern.compile("([А-я]+)\\s+([А-я]+)(?:\\s+([А-я]+))?\\s+(\\d{6})");

    public void registerUserByNameAndGroup(UserDetails userDetails, String userRegistrationMessage) {
        Matcher matcher = STUDENT_REGISTRATION_MESSAGE_PATTERN.matcher(userRegistrationMessage);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Неверный формат сообщения");
        }

        String firstName = matcher.group(1);
        String lastName = matcher.group(2);
        String middleName = matcher.group(3) != null ? matcher.group(3).trim() : "";
        String group = matcher.group(4);

        String name = String.format("%s %s %s", firstName, lastName, middleName).trim();

        List<List<String>> students = spreadsheetsService.getStudents();

        for (List<String> student : students) {
            if (student.size() < 2) continue;

            String studentName = student.get(0).trim();
            String studentGroup = student.get(1).trim();
            String telegramId = student.size() > 2 ? student.get(2).trim() : "";

            if (studentName.equalsIgnoreCase(name) && studentGroup.equals(group)) {
                if (!telegramId.isEmpty()) {
                    throw new IllegalArgumentException("Студент с таким именем и группой уже зарегистрирован");
                }

                userDetails.setName(name);
                userDetails.setRole(Role.STUDENT);
                dbUserService.updateUser(userDetails);
                spreadsheetsService.updateTelegramIdForStudentByNameAndGroup(studentName, studentGroup, userDetails.getId().toString());

                return;
            }
        }
        throw new IllegalArgumentException("Студент не найден в списке");
    }
}
