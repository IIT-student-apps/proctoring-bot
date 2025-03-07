package org.bsuir.proctoringbot.service;

import lombok.RequiredArgsConstructor;
import org.bsuir.proctoringbot.bot.exception.UserNotFoundException;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.bot.security.UserService;
import org.bsuir.proctoringbot.bot.statemachine.State;
import org.bsuir.proctoringbot.model.SimpleTelegramUser;
import org.bsuir.proctoringbot.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
public class DbUserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails getUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("no such user"));
    }

    @Override
    public UserDetails getUserByUsername(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("no such user"));
    }

    @Override
    public UserDetails createEmptyUser(Long id, String username) {
        SimpleTelegramUser user = SimpleTelegramUser.builder().name("").username(username).id(id).build();
        userRepository.save(user);
        return user;
    }

    @Override
    public UserDetails createUser(Long userId, String name, String username, Role role, State state) {
        return userRepository.save(SimpleTelegramUser.builder()
                .id(userId)
                .name(name)
                .username(username)
                .role(role)
                .state(state)
                .build());
    }

    @Override
    public void updateUser(UserDetails userDetails) {
        userRepository.save(SimpleTelegramUser.builder()
                .id(userDetails.getId())
                .name(userDetails.getName())
                .username(userDetails.getUsername())
                .role(userDetails.getRole())
                .state(userDetails.getState())
                .build());
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.findById(id).isPresent();
    }


}
