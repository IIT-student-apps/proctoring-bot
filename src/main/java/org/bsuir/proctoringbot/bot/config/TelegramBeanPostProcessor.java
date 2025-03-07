package org.bsuir.proctoringbot.bot.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsuir.proctoringbot.bot.security.AllowedRoles;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramController;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequestMapping;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramUpdateDispatcher;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBeanPostProcessor implements BeanPostProcessor {


    private final TelegramUpdateDispatcher dispatcher;

    @Override
    public Object postProcessAfterInitialization(Object bean, @NonNull String beanName) {
        Class<?> beanClass = bean.getClass();

        if (!beanClass.isAnnotationPresent(TelegramController.class)) {
            return bean;
        }

        log.info("Registered new controller {}", beanName);
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(TelegramRequestMapping.class)) {
                TelegramRequestMapping annotation = method.getAnnotation(TelegramRequestMapping.class);
                State startState = annotation.from();
                State endState = annotation.to();
                Set<Role> roles = Collections.emptySet();
                if (method.isAnnotationPresent(AllowedRoles.class)) {
                    AllowedRoles allowedRoles = method.getAnnotation(AllowedRoles.class);
                    roles = Set.of(allowedRoles.value());
                }
                dispatcher.registerHandler(bean, method, startState, endState, roles);
            }
        }

        return bean;
    }

}
