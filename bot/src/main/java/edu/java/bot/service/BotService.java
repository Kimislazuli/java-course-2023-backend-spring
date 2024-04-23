package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot_logic.Bot;
import edu.java.models.dto.LinkUpdate;
import edu.java.models.dto.UpdateType;
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
            if (update.updateType().equals(UpdateType.DEFAULT)) {
                bot.sendMessage(new SendMessage(tgChatId, String.format("Ссылка %s обновилась", update.url())));
            } else if (update.updateType().equals(UpdateType.PUSH)) {
                bot.sendMessage(new SendMessage(
                    tgChatId,
                    String.format("Ссылка %s обновилась.\nПоявился новый push от %s",
                        update.url(),
                        update.description()
                    )
                ));
            } else if (update.updateType().equals(UpdateType.PR_MERGE)) {
                bot.sendMessage(new SendMessage(
                    tgChatId,
                    String.format("Ссылка %s обновилась.\nMerge PR от %s",
                        update.url(),
                        update.description()
                    )
                ));
            }
        }
    }
}
