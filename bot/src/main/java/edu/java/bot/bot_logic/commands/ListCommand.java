package edu.java.bot.bot_logic.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.LinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("list")
@Slf4j
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
        Long chatId = update.message().chat().id();
        String linkList = linkService.list(chatId);
        return linkList.isEmpty() ? new SendMessage(chatId, "Нет зарегистрированных ссылок.")
            : new SendMessage(chatId, linkList);
    }
}
