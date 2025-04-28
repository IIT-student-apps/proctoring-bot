package org.bsuir.proctoringbot.jobs;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.model.Test;
import org.bsuir.proctoringbot.model.TestStatus;
import org.bsuir.proctoringbot.repository.TestRepository;
import org.bsuir.proctoringbot.service.TestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class TestCheckJob {

    private final TestService testService;

    private final TestRepository testRepository;

    public void checkTests(LocalDateTime threshold) {
        Iterable<Test> allWithStartTimeBefore = testRepository.findAllWithStartTimeBeforeAndStatus(threshold, TestStatus.CREATED);
        for (Test test : allWithStartTimeBefore) {
            testService.writeMarks(test);
            test.setStatus(TestStatus.CHECKED);
            testRepository.save(test);
        }
    }

}
