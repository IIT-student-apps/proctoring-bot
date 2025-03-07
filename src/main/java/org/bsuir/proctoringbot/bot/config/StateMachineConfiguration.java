package org.bsuir.proctoringbot.bot.config;

import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.bsuir.proctoringbot.bot.statemachine.StateMachine;
import org.bsuir.proctoringbot.bot.statemachine.StudentStateMachine;
import org.bsuir.proctoringbot.bot.statemachine.TeacherStateMachine;
import org.bsuir.proctoringbot.bot.statemachine.UserStateMachine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class StateMachineConfiguration {

    @Bean
    public Map<Role, StateMachine> stateMachine(StateMachine userStateMachine,
                                                StateMachine teacherStateMachine,
                                                StateMachine studentStateMachine) {
        return Map.of(
                Role.USER, userStateMachine,
                Role.TEACHER, teacherStateMachine,
                Role.STUDENT, studentStateMachine
        );
    }

    @Bean
    public StateMachine userStateMachine(){
        return UserStateMachine.create()
                .registerState(State.NEW)
                .add(State.REGISTRATION)
                .build();
    }

    @Bean
    public StateMachine teacherStateMachine(){
        return TeacherStateMachine.create()
                .registerState(State.NEW_TEACHER)
                .add(State.MENU_TEACHER)
                .build();
    }

    @Bean
    public StateMachine studentStateMachine(){
        return StudentStateMachine.create()
                .registerState(State.NEW_STUDENT)
                .add(State.MENU_STUDENT)
                .build();
    }

}
