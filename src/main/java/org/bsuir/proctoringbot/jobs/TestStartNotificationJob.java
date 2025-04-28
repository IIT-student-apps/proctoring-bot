package org.bsuir.proctoringbot.jobs;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.TelegramBot;
import org.bsuir.proctoringbot.model.Test;
import org.bsuir.proctoringbot.repository.TestRepository;
import org.bsuir.proctoringbot.service.SpreadsheetsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TestStartNotificationJob {

    private final TestRepository testRepository;
    private final SpreadsheetsService spreadsheetsService;
    private final TelegramBot telegramBot;

    private static final String TEST_START_NOTIFICATION_MESSAGE_PATTERN = "Преподаватель %s, начал тест '%s' по ссылке: %s";

    public void notifyAutomatically() {
        Iterable<Test> allByStartTime = testRepository.findAllByStartTime(LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
        for (Test test : allByStartTime) {
            makeNotification(test);
        }
    }

    public void notifyManually(Test test){
        makeNotification(test);
        test.setStartTime(LocalDateTime.now());
        testRepository.save(test);
    }

    private void makeNotification(Test test){
        List<Long> studentsTgIdsByGroup = spreadsheetsService.getStudentsTgIdsByGroup(test.getGroupNumber());
        String teacherName = test.getAuthor().getName();
        String testName = test.getName();
        String url = test.getUrl();
        for (Long id : studentsTgIdsByGroup) {
            notifyUser(id, teacherName, testName, url);
        }
    }

    private void notifyUser(Long tgId, String teacherName, String testName,String url){
        String message = String.format(TEST_START_NOTIFICATION_MESSAGE_PATTERN, teacherName, testName, url);
        telegramBot.sendNotification(tgId, message);
    }

}
