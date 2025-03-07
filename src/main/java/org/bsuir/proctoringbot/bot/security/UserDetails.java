package org.bsuir.proctoringbot.bot.security;

import org.bsuir.proctoringbot.bot.statemachine.State;

public interface UserDetails {

    Long getId();
    String getName();
    void setName(String name);
    String getUsername();
    void setUsername(String username);
    Role getRole();
    void setRole(Role role);
    State getState();
    void setState(State state);

}
