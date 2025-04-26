package org.bsuir.proctoringbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsuir.proctoringbot.jobs.TestStartNotificationJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledJobsService {

    private final TestStartNotificationJob testStartNotificationJob;

    @Scheduled(cron = "${job.cron.test-start}")
    public void scheduleTestStartNotification(){
        log.info("Start test notification");
        testStartNotificationJob.notifyAutomatically();
        log.info("Start test notification ended");
    }

}
