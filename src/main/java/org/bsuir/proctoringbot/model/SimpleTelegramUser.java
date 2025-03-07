package org.bsuir.proctoringbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.bsuir.proctoringbot.bot.security.Role;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.bsuir.proctoringbot.bot.statemachine.State;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_details")
public class SimpleTelegramUser implements UserDetails {

    @Id
    @Column(name = "chat_id")
    private Long id;
    private String name;
    @Column(unique = true)
    private String username;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private State state = State.NEW;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Role getRole() {
        return role;
    }

    @Override
    public void setRole(Role role) {
        this.role = role;
    }


    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }


}
