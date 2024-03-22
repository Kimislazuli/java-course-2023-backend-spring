package edu.java.bot.model;

import edu.java.bot.bot_logic.State;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Chat {
    private final Long telegramId;

    @Setter
    private State state;

    public Chat(Long telegramId) {
        this.telegramId = telegramId;
        this.state = State.DEFAULT;
    }

    public Chat(Long telegramId, int state) {
        this.telegramId = telegramId;
        this.state = State.values()[state];
    }
}
