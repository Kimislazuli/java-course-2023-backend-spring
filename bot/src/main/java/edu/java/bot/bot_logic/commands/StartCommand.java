package edu.java.bot.bot_logic.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("start")
@Slf4j
public class StartCommand implements Command {
    private final ScrapperClient client;

    @Autowired
    public StartCommand(ScrapperClient client) {
        this.client = client;
    }

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Команда регистрации в боте.";
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();
        try {
            client.registerChat(chatId);
            log.info("Чат {} успешно зарегистрирован.", chatId);
            return new SendMessage(update.message().chat().id(), "Успешная авторизация.");
        } catch (Exception e) {
            log.error("Чат {} уже зарегистрирован.", chatId);
            return new SendMessage(update.message().chat().id(), "Вы уже авторизованы.");
        }
    }
}
