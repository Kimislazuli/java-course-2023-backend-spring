package edu.java.bot.botLogic.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("track")
public class TrackCommand implements Command {
    private final LinkService linkService;

    @Autowired
    public TrackCommand(LinkService linkService) {
        this.linkService = linkService;
    }

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public String description() {
        return "добавить ссылку для отслеживания";
    }

    @Override
    public SendMessage handle(Update update) {
        Long userId = update.message().chat().id();
        String link = update.message().text();
        linkService.track(userId, link);
        return new SendMessage(update.message().chat().id(), String.format("Ссылка %s успешно добавлена.", link));
    }
}
