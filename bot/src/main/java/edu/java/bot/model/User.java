package edu.java.bot.model;

import edu.java.bot.botLogic.State;
import lombok.Getter;
import lombok.Setter;

public class User {
    @Getter
    private final Long telegramId;

    @Setter
    @Getter
    private State state;

    public User(Long telegramId) {
        this.telegramId = telegramId;
        this.state = State.DEFAULT;
    }
}
