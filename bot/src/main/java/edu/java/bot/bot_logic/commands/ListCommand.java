package edu.java.bot.bot_logic.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.LinkService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("list")
public class ListCommand implements Command {
    private final LinkService linkService;

    @Autowired
    public ListCommand(LinkService linkService) {
        this.linkService = linkService;
    }

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "список ссылок";
    }

    @Override
    public SendMessage handle(Update update) {
        Long userId = update.message().chat().id();
        String message = linkService.list(userId);
        return new SendMessage(
            update.message().chat().id(),
            Objects.requireNonNullElse(message, "Нет зарегистрированных ссылок.")
        );
    }
}
