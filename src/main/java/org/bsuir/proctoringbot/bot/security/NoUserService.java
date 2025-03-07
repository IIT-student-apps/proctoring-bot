package org.bsuir.proctoringbot.bot.security;

import org.bsuir.proctoringbot.bot.exception.UserNotFoundException;
import org.bsuir.proctoringbot.bot.statemachine.State;

public class NoUserService implements UserService {

    @Override
    public UserDetails getUserById(Long id) throws UserNotFoundException {
        throw new UserNotFoundException("no such user");
    }

    @Override
    public UserDetails getUserByUsername(String username) throws UserNotFoundException {
        throw new UserNotFoundException("no such user");
    }

    @Override
    public UserDetails createEmptyUser(Long id, String username) {
        return null;
    }

    @Override
    public UserDetails createUser(Long userId, String name, String username, Role role, State state) {
        return null;
    }

    @Override
    public void updateUser(UserDetails userDetails) {

    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

}
