package org.bsuir.proctoringbot.bot.dispatcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.statemachine.State;

import java.lang.reflect.Method;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class StateMethodHandler {

    private final Object bean;
    private final Method method;
    private final State fromState;
    private final State toState;
    private final Set<Role> roleSet;

}
