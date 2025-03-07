package org.bsuir.proctoringbot.bot.security;

import org.bsuir.proctoringbot.bot.exception.UserNotFoundException;
import org.bsuir.proctoringbot.bot.statemachine.State;

public interface UserService {


    /**
     * @param id telegram user id
     * @return User entity loaded by id
     * @throws UserNotFoundException if user with such user id does not exist
     */
    UserDetails getUserById(Long id) throws UserNotFoundException;

    UserDetails getUserByUsername(String username) throws UserNotFoundException;

    UserDetails createEmptyUser(Long id, String username);

    UserDetails createUser(Long userId, String name, String username, Role role, State state);

    void updateUser(UserDetails userDetails);

    boolean existsById(Long id);

}
