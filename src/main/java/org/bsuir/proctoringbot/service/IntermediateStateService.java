package org.bsuir.proctoringbot.service;

import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.model.IntermediateStateData;

public interface IntermediateStateService {

    void updateIntermediateState(UserDetails userDetails, IntermediateStateData intermediateStateData);

}
