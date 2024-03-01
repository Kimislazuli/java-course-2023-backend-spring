package edu.java.bot.bot_logic.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("untrack")
public class UntrackCommand implements Command {
    private final LinkService linkService;

    @Autowired
    public UntrackCommand(LinkService linkService) {
        this.linkService = linkService;
    }

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "перестать отслеживать ссылку";
    }

    @Override
    public SendMessage handle(Update update) {
        Long userId = update.message().chat().id();
        String link = update.message().text();
        boolean deleted = linkService.untrack(userId, link);
        if (deleted) {
            return new SendMessage(update.message().chat().id(), String.format("Ссылка %s успешно удалена.", link));
        }
        return new SendMessage(
            update.message().chat().id(),
            String.format("Ссылка %s не была ранее зарегистрирована.", link)
        );
    }
}
