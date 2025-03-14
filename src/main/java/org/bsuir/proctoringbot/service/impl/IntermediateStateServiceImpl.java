package org.bsuir.proctoringbot.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.model.IntermediateState;
import org.bsuir.proctoringbot.model.IntermediateStateData;
import org.bsuir.proctoringbot.model.SimpleTelegramUser;
import org.bsuir.proctoringbot.repository.IntermediateStateRepository;
import org.bsuir.proctoringbot.service.IntermediateStateService;
import org.springframework.stereotype.Service;

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

    private IntermediateStateData buildIntermediateStateData(
            IntermediateStateData previousState,
            IntermediateStateData newState
    ) {
        return IntermediateStateData.builder()
                .pickedWorkType(newState.getPickedWorkType() == null ? previousState.getPickedWorkType() : newState.getPickedWorkType())
                .pickedSubject(newState.getPickedSubject() == null ? previousState.getPickedSubject() : newState.getPickedSubject())
                .build();
    }
}
