package org.bsuir.proctoringbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsuir.proctoringbot.jobs.TestCheckJob;
import org.bsuir.proctoringbot.jobs.TestStartNotificationJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledJobsService {

    private final TestStartNotificationJob testStartNotificationJob;

    private final TestCheckJob testCheckJob;

    @Scheduled(cron = "${job.cron.test-start}")
    public void scheduleTestStartNotification() {
        log.info("Start test notification");
        testStartNotificationJob.notifyAutomatically();
        log.info("Start test notification ended");
    }

    @Scheduled(cron = "${job.cron.test-check}")
    public void scheduleTestCheckJob() {
        log.info("Start test check job");
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        testCheckJob.checkTests(threshold);
        log.info("End test check job");
    }

}
