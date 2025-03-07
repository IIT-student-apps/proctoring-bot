package org.bsuir.proctoringbot.service;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.exception.UserNotFoundException;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.bot.security.UserService;
import org.bsuir.proctoringbot.bot.statemachine.State;

@RequiredArgsConstructor
public class SpreadsheetsUserServiceImpl implements UserService {

    private final SpreadsheetsService spreadsheetsService;

    @Override
    public UserDetails getUserById(Long id) throws UserNotFoundException {
        throw new UserNotFoundException("User not found");
    }

    @Override
    public UserDetails getUserByUsername(String username) throws UserNotFoundException {
        return spreadsheetsService.findTeacherByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public UserDetails createEmptyUser(Long id, String username) {
       throw new RuntimeException();
    }

    @Override
    public UserDetails createUser(Long userId, String name, String username, Role role, State state) {
        throw new RuntimeException();
    }

    @Override
    public void updateUser(UserDetails userDetails) {
        throw new RuntimeException();
    }

    @Override
    public boolean existsById(Long id) {
        throw new RuntimeException();
    }
}
