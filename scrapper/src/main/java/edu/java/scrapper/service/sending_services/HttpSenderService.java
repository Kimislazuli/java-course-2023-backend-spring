package edu.java.scrapper.service.sending_services;

import edu.java.models.dto.LinkUpdate;
import edu.java.scrapper.client.BotClient;

public class HttpSenderService implements SenderService {
    private final BotClient client;

    public HttpSenderService(BotClient client) {
        this.client = client;
    }

    @Override
    public void send(LinkUpdate update) {
        client.updates(update.id(), update.url(), update.description(), update.tgChatIds());
    }
}
