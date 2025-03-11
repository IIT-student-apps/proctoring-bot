package org.bsuir.proctoringbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsuir.proctoringbot.jobs.TestStartNotificationJobService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledJobsService {

    private final TestStartNotificationJobService testStartNotificationJobService;

    @Scheduled(cron = "${job.cron.test-start}")
    public void scheduleTestStartNotification(){
        log.info("Start test notification");
        testStartNotificationJobService.notifyAutomatically();
        log.info("Start test notification ended");
    }

}
