package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot_logic.Bot;
import edu.java.models.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotService {
    private final Bot bot;

    public void sendUpdatesInfo(LinkUpdate update) {
        log.info(String.valueOf(update));
        for (Long tgChatId : update.tgChatIds()) {
            bot.sendMessage(new SendMessage(tgChatId, String.format("Ссылка %s обновилась", update.url())));
        }
    }
}
