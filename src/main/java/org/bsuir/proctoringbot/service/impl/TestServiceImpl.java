package org.bsuir.proctoringbot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.jobs.TestStartNotificationJob;
import org.bsuir.proctoringbot.model.SimpleTelegramUser;
import org.bsuir.proctoringbot.model.Test;
import org.bsuir.proctoringbot.repository.TestRepository;
import org.bsuir.proctoringbot.service.SpreadsheetsService;
import org.bsuir.proctoringbot.service.TestService;
import org.bsuir.proctoringbot.util.SpreadsheetsUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class TestServiceImpl implements TestService {

    private static final Pattern TEST_CREATION_PATTERN = Pattern.compile("^([^,]+),\\s*(https?://\\S+),\\s*(https?://\\S+),\\s*(\\d+)(?:,\\s*(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}))?$");

    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm";

    private final TestRepository testRepository;
    private final SpreadsheetsService spreadsheetsService;
    private final TestStartNotificationJob testStartNotificationJob;

    public List<Test> getAllUnactivatedTests(UserDetails userDetails) {
        return testRepository.findAllByAuthorIdAndStartTimeIsNull(userDetails.getId());
    }

    @Override
    public void addTest(UserDetails userDetails, String message) {
        Matcher matcher = TEST_CREATION_PATTERN.matcher(message);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Неправильный формат создания теста");
        }
        String name = matcher.group(1);
        String url = matcher.group(2);
        String tableLink = matcher.group(3);
        String group = matcher.group(4);
        if (testRepository.existsByName(name)) {
            throw new IllegalArgumentException("Тест с таким названием уже существует, попробуйте другое");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
            LocalDateTime startTime = null;
            if (matcher.group(5) != null) {
                startTime = LocalDateTime.parse(matcher.group(4), formatter);
            }
            if (!isUserCanAddTestToGroup(userDetails, group)) {
                throw new IllegalArgumentException("Вы не ведёте предметы у такой группы");
            }
            if (startTime != null && !startTime.isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Время начала теста уже прошло");
            }
            Test test = Test.builder()
                    .author((SimpleTelegramUser) userDetails)
                    .name(name)
                    .url(url)
                    .groupNumber(group)
                    .startTime(startTime)
                    .tableLink(tableLink)
                    .build();
            testRepository.save(test);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Время начала указано неверно");
        }
    }

    @Override
    public void activateTest(UserDetails userDetails, String testName) {
        Test test = testRepository.findByName(testName)
                .orElseThrow(() -> new IllegalArgumentException("Такого теста не существует"));
        testStartNotificationJob.notifyManually(test);
    }

    @Override
    public void writeMarks(Test test) {
        String subjectTableSpreadsheetId = getSubjectTableSpreadsheetsId(test);
        String tableLinkSpreadsheetId = SpreadsheetsUtil.getSpreadsheetId(test.getTableLink());
        List<String> formedColumnOfMarks = getFormedColumnOfMarks(test, tableLinkSpreadsheetId, subjectTableSpreadsheetId);
        System.out.println(formedColumnOfMarks);
        spreadsheetsService.writeColumnOfMarks(subjectTableSpreadsheetId, test.getGroupNumber(), formedColumnOfMarks);
    }

    private boolean isUserCanAddTestToGroup(UserDetails userDetails, String groupNumber) {
        List<String> teachersGroups = spreadsheetsService.getTeachersGroups(userDetails);
        for (String teachersGroup : teachersGroups) {
            if (teachersGroup.equals(groupNumber)) {
                return true;
            }
        }
        return false;
    }

    private String getSubjectTableSpreadsheetsId(Test test) {
        return SpreadsheetsUtil.getSpreadsheetId(
                spreadsheetsService.getSubjectSpreadsheetURLByGroupAndUsername(
                        test.getGroupNumber(),
                        test.getAuthor().getUsername()
                )
        );
    }

    private List<String> getFormedColumnOfMarks(Test test, String tableLinkSpreadsheetId, String subjectTableSpreadsheetId) {
        List<List<String>> answersFromGoogleForm = spreadsheetsService.getAllAnswers(tableLinkSpreadsheetId);
        List<String> FIOs = spreadsheetsService.getAllFIOs(subjectTableSpreadsheetId, test.getGroupNumber());
        return formColumnOfMarks(test, answersFromGoogleForm, FIOs);
    }

    private List<String> formColumnOfMarks(Test test, List<List<String>> answersFromGoogleForm, List<String> FIOs) {
        List<String> columnOfMarks = new ArrayList<>();
        columnOfMarks.add(test.getName());
        columnOfMarks.add(test.getUrl());
        List<String> FIOsFromTest = answersFromGoogleForm.get(0);
        for (String fio : FIOs) {
            int index = FIOsFromTest.indexOf(fio);
            String grade = "0";
            if (FIOsFromTest.contains(fio)) {
                grade = answersFromGoogleForm.get(1).get(index);
            }
            columnOfMarks.add(grade);
        }
        return columnOfMarks;
    }
}
