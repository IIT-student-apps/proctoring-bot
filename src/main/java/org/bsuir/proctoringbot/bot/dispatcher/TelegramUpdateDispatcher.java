package org.bsuir.proctoringbot.bot.dispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsuir.proctoringbot.bot.exception.TelegramMessageException;
import org.bsuir.proctoringbot.bot.security.AuthenticationManager;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.bot.security.UserService;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.bsuir.proctoringbot.util.TelegramUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUpdateDispatcher {

    private static final String REQUEST_REFRESH_STATE_MESSAGE = "refresh_state_01";

    private final Map<State, StateMethodHandler> handlerMap = new HashMap<>();
    private final AuthenticationManager authenticationManager;
    private final UserService dbUserService;


    public void registerHandler(Object bean, Method method,
                                State startState, State endState, Set<Role> roleSet) {
        handlerMap.put(startState, new StateMethodHandler(bean, method, startState, endState, roleSet));
    }

    public void dispatch(TelegramRequest request, TelegramResponse response) throws Throwable {
        try {

            UserDetails user = authenticationManager.authenticate(
                    TelegramUtil.getChatId(request.getUpdate()),
                    TelegramUtil.getUserName(request.getUpdate())
            );

            request.setUser(user);

            if (checkAndRefreshUserState(user, request)){
                response.setResponse(SendMessage.builder()
                        .chatId(TelegramUtil.getChatId(request.getUpdate()))
                        .text("Состояние успешно сброшено")
                        .build());
                return;
            }

            StateMethodHandler handler = handlerMap.get(user.getState());

            if (handler == null) {
                log.info("No handler registered for {} in state {}", user.getUsername(), user.getState());
                throw new TelegramMessageException("Нет контроллера для вышего состояния");
            }

            boolean hasRequiredRole = handler.getRoleSet().stream()
                    .anyMatch(role -> user.getRole().equals(role));

            if (!hasRequiredRole && !handler.getRoleSet().isEmpty()) {
                throw new TelegramMessageException("Кажется, вы не тот за кого себя выдаёте");
            }

            handler.getMethod().invoke(handler.getBean(), request, response);

            if (handler.getToState() != State.NONE){
                user.setState(handler.getToState());
                dbUserService.updateUser(user);
            }

        } catch(InvocationTargetException ex){
            throw ex.getCause();
        } catch(Exception e){
            log.error("Caught unhandled exception", e);
            throw e;
        }
    }

    private boolean checkAndRefreshUserState(UserDetails userDetails, TelegramRequest req){
        if(!req.getUpdate().hasMessage() || !REQUEST_REFRESH_STATE_MESSAGE.equals(req.getUpdate().getMessage().getText())){
            return false;
        }
        if (userDetails.getRole() == Role.STUDENT){
            userDetails.setState(State.MENU_STUDENT);
        } else if (userDetails.getRole() == Role.TEACHER) {
            userDetails.setState(State.MENU_TEACHER);
        } else {
            return false;
        }
        dbUserService.updateUser(userDetails);
        return true;
    }

}
