package edu.java.bot.bot_logic.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("start")
public class StartCommand implements Command {
    private final UserRepository userRepository;
    private final ScrapperClient client;

    @Autowired
    public StartCommand(UserRepository userRepository, ScrapperClient client) {
        this.userRepository = userRepository;
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
        Long userId = update.message().chat().id();
        if (userRepository.isAuthenticated(userId)) {
            return new SendMessage(update.message().chat().id(), "Вы уже авторизованы.");
        }
        userRepository.addUser(userId);
        client.registerChat(userId);
        return new SendMessage(update.message().chat().id(), "Успешная авторизация.");
    }
}
