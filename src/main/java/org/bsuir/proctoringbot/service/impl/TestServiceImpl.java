package org.bsuir.proctoringbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.model.SimpleTelegramUser;
import org.bsuir.proctoringbot.model.Test;
import org.bsuir.proctoringbot.repository.TestRepository;
import org.bsuir.proctoringbot.service.SpreadsheetsService;
import org.bsuir.proctoringbot.service.TestService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private static final Pattern TEST_CREATION_PATTERN = Pattern.compile("^([^,]+),\\s*(https?://\\S+),\\s*(\\d+)(?:,\\s*(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}))?$");
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm";

    private final TestRepository testRepository;
    private final SpreadsheetsService spreadsheetsService;

    @Override
    public List<Test> getAllTests(UserDetails userDetails) {
        return testRepository.findAllByAuthorId(userDetails.getId());
    }

    @Override
    public void addTest(UserDetails userDetails, String message) {
        Matcher matcher = TEST_CREATION_PATTERN.matcher(message);
        if (!matcher.matches()){
            throw new IllegalArgumentException("Неправильный формат создания теста");
        }
        String name = matcher.group(1);
        String url = matcher.group(2);
        String group = matcher.group(3);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
            LocalDateTime startTime = null;
            if (matcher.group(4) != null) {
                startTime = LocalDateTime.parse(matcher.group(4), formatter);
            }
            if (!isUserCanAddTestToGroup(userDetails, group)){
                throw new IllegalArgumentException("Вы не ведёте предметы у такой группы");
            }
            Test test = Test.builder()
                    .author((SimpleTelegramUser) userDetails)
                    .name(name)
                    .url(url)
                    .groupNumber(group)
                    .startTime(startTime)
                    .build();
            testRepository.save(test);
        } catch (DateTimeParseException e){
            throw new IllegalArgumentException("Время начала указано неверно");
        }
    }

    private boolean isUserCanAddTestToGroup(UserDetails userDetails, String groupNumber){
        List<String> teachersGroups = spreadsheetsService.getTeachersGroups(userDetails);
        for (String teachersGroup : teachersGroups) {
            if (teachersGroup.equals(groupNumber)){
                return true;
            }
        }
        return false;
    }

}
