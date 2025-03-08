package org.bsuir.proctoringbot.transformer;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubjectTransformer {
    public String transform(List<List<String>> subjectsFromTable) {
        int number = 1;
        StringBuilder result = new StringBuilder();
        for(List<String> row : subjectsFromTable) {
            result.append(String.format("%s. %s %s\n", number, row.get(0), row.get(1)));
            number++;
        }
        return result.toString();
    }
}
