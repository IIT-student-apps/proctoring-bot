package org.bsuir.proctoringbot.transformer;

import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.bsuir.proctoringbot.util.Regex.SEPARATOR_REGEX;

@Component
public class SubjectTransformer {
    public String transformForGetAllSubjects(List<List<String>> subjectsFromTable) {
        int number = 1;
        StringBuilder result = new StringBuilder();
        for (List<String> row : subjectsFromTable) {
            result.append(String.format("%s. %s %s\n", number, row.get(0), row.get(1)));
            number++;
        }
        return result.toString();
    }

    public List<List<String>> transformForAddSubject(String subjectRequest, UserDetails userDetails) {
        String[] parts = subjectRequest.split(SEPARATOR_REGEX, 3);
        String[] groups = parts[2].split(SEPARATOR_REGEX);
        List<List<String>> subjects = new ArrayList<>();
        for (String group : groups) {
            List<String> row = List.of(parts[0], group, parts[1], userDetails.getUsername());
            subjects.add(row);
        }
        return subjects;
    }
}
