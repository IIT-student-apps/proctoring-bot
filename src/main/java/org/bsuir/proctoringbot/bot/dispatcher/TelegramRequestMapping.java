package org.bsuir.proctoringbot.bot.dispatcher;

import org.bsuir.proctoringbot.bot.statemachine.State;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.bsuir.proctoringbot.bot.statemachine.State.NONE;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TelegramRequestMapping {

    State from();
    State to() default NONE;

}
