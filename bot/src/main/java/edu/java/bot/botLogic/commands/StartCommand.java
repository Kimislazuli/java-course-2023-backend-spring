package edu.java.bot.botLogic.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("start")
public class StartCommand implements Command {
    private final UserRepository userRepository;

    @Autowired
    public StartCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return new SendMessage(update.message().chat().id(), "Успешная авторизация.");
    }
}
