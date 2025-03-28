package org.bsuir.proctoringbot.service;

import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.model.IntermediateState;
import org.bsuir.proctoringbot.model.IntermediateStateData;

import java.util.Optional;

public interface IntermediateStateService {

    void updateIntermediateState(UserDetails userDetails, IntermediateStateData intermediateStateData);

    Optional<IntermediateState> findIntermediateStateByUserId(Long userId);

    IntermediateState getIntermediateState(UserDetails userDetails);
}
