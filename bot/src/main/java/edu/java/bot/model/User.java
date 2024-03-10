package edu.java.bot.model;

import edu.java.bot.bot_logic.State;
import lombok.Getter;
import lombok.Setter;

@Getter
public class User {
    private final Long telegramId;

    @Setter
    private State state;

    public User(Long telegramId) {
        this.telegramId = telegramId;
        this.state = State.DEFAULT;
    }
}
