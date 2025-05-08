package org.bsuir.proctoringbot.bot.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsuir.proctoringbot.bot.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationManager {

    private final UserService spreadsheetsUserService;
    private final UserService dbUserService;

    public UserDetails authenticate(Long userId, String username) {
        try {
            if (dbUserService.existsById(userId)) {
                return dbUserService.getUserById(userId);
            }
            UserDetails userDetails = spreadsheetsUserService.getUserByUsername(username);
            return dbUserService.createUser(userId,
                    userDetails.getName(),
                    userDetails.getUsername(),
                    userDetails.getRole(),
                    userDetails.getState());
        } catch (UserNotFoundException e) {
            return dbUserService.createEmptyUser(userId, username);
        }
    }

}
