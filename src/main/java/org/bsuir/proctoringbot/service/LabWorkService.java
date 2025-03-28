package org.bsuir.proctoringbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsuir.proctoringbot.bot.exception.TelegramMessageException;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.model.IntermediateState;
import org.bsuir.proctoringbot.model.LabWork;
import org.bsuir.proctoringbot.model.SimpleTelegramUser;
import org.bsuir.proctoringbot.repository.LabWorkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LabWorkService {

    private final IntermediateStateService intermediateStateService;
    private final GoogleDriveService googleDriveService;
    private final LabWorkRepository labWorkRepository;

    public void saveLabWork(UserDetails user, String link){
        if(!googleDriveService.isAccessibleLink(link)){
            throw new TelegramMessageException("Ссылка неккоректная или нет доступа для просмотра");
        }
        IntermediateState intermediateState = intermediateStateService.getIntermediateState(user);
        String labWorkNumber = intermediateState.getState().getPickedLabWorkNumber();
        String subject = intermediateState.getState().getPickedSubject();
        if (labWorkNumber == null || subject == null){
            log.warn("user {} tried to save lab work without subject or number", user.getName());
            throw new TelegramMessageException("Вы не можете скинуть лабораторную работу(");
        }
        LabWork labWork = LabWork.builder()
                .user((SimpleTelegramUser) user)
                .subject(subject)
                .labNumber(labWorkNumber)
                .link(link)
                .build();

        labWorkRepository.save(labWork);
    }

}
