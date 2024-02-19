package edu.java.bot.model;

import lombok.Getter;

public class Link {
    @Getter
    private final String url;

    public Link(String url) {
        this.url = url;
    }
}
