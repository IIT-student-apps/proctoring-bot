package org.bsuir.proctoringbot.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.exception.TelegramMessageException;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.model.IntermediateState;
import org.bsuir.proctoringbot.model.IntermediateStateData;
import org.bsuir.proctoringbot.model.SimpleTelegramUser;
import org.bsuir.proctoringbot.repository.IntermediateStateRepository;
import org.bsuir.proctoringbot.service.IntermediateStateService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IntermediateStateServiceImpl implements IntermediateStateService {

    private final IntermediateStateRepository intermediateStateRepository;

    @Override
    @Transactional
    public void updateIntermediateState(UserDetails userDetails, IntermediateStateData intermediateStateData) {
        intermediateStateRepository.findIntermediateStateByUserId(userDetails.getId())
                .ifPresentOrElse(
                        state -> {
                            IntermediateStateData newState = buildIntermediateStateData(state.getState(), intermediateStateData);
                            state.setState(newState);
                            intermediateStateRepository.save(state);
                        },
                        () -> intermediateStateRepository.save(
                                IntermediateState.builder()
                                        .state(intermediateStateData)
                                        .user((SimpleTelegramUser) userDetails)
                                        .build()
                        )
                );
    }

    @Override
    public Optional<IntermediateState> findIntermediateStateByUserId(Long userId) {
        return intermediateStateRepository.findIntermediateStateByUserId(userId);
    }

    @Override
    @Transactional
    public IntermediateState getIntermediateState(UserDetails userDetails) {
        return intermediateStateRepository.findIntermediateStateByUserId(userDetails.getId()).orElseThrow(() -> new TelegramMessageException("вы не выбрали предмет"));
    }

    private IntermediateStateData buildIntermediateStateData(
            IntermediateStateData previousState,
            IntermediateStateData newState
    ) {
        return IntermediateStateData.builder()
                .pickedLabWorkNumber(newState.getPickedLabWorkNumber() == null ? previousState.getPickedLabWorkNumber() : newState.getPickedLabWorkNumber())
                .pickedSubject(newState.getPickedSubject() == null ? previousState.getPickedSubject() : newState.getPickedSubject())
                .build();
    }
}
