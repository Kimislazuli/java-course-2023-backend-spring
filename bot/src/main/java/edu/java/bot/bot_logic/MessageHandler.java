package edu.java.bot.bot_logic;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot_logic.commands.Command;
import edu.java.bot.bot_logic.commands.HelpCommand;
import edu.java.bot.model.User;
import edu.java.bot.repository.UserRepository;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@SuppressWarnings("MissingSwitchDefault")
public class MessageHandler {
    private static final String WRONG_COMMAND_MESSAGE =
        "Я не понимаю, какой функцией Вы хотите воспользоваться. "
            + "Введите команду /help, чтобы узнать, что я умею.";
    private static final String NOT_AVAILABLE_COMMAND_MESSAGE =
        "Используйте комманду /start, чтобы зарегистрироваться.";
    private static final String WRONG_LINK_MESSAGE = "Некорректная ссылка.";
    private static final String HELP = "/help";
    private static final String START = "/start";
    private static final String TRACK = "/track";
    private static final String UNTRACK = "/untrack";
    private static final String LIST = "/list";
    private static final int OK = 200;

    private final Map<String, Command> commands;

    private final UserRepository userRepository;

    @Autowired
    public MessageHandler(Set<? extends Command> commandsSet, UserRepository repository) {
        commands = commandsSet.stream().collect(Collectors.toMap(Command::command, Function.identity()));
        HelpCommand help = (HelpCommand) commands.get(HELP);
        help.setCommands(commandsSet);
        userRepository = repository;
    }

    public SendMessage handleRequest(Update update) {
        String messageText = update.message().text();
        Long userId = update.message().chat().id();
        User user = userRepository.getUserById(userId);

        if (commands.containsKey(messageText)) {
            return handleCommandInput(update, messageText, userId, user);
        } else if (user != null) {
            return handleOtherInput(update, messageText, userId, user);
        }
        return new SendMessage(userId, WRONG_COMMAND_MESSAGE);
    }

    private SendMessage handleCommandInput(Update update, String messageText, Long userId, User user) {
        Command command = commands.get(messageText);
        if (user == null || command.command().equals(LIST)) {
            if (List.of(HELP, START).contains(messageText) || (command.command().equals(LIST) && user != null)) {
                return command.handle(update);
            }
            return new SendMessage(userId, NOT_AVAILABLE_COMMAND_MESSAGE);
        }
        switch (messageText) {
            case START -> {
                return new SendMessage(userId, "Вы уже авторизованы");
            }
            case TRACK -> user.setState(State.WAIT_FOR_LINK_TO_ADD);
            case UNTRACK -> user.setState(State.WAIT_FOR_LINK_TO_REMOVE);
        }
        return new SendMessage(userId, "Отправьте ссылку");

    }

    private SendMessage handleOtherInput(Update update, String messageText, Long userId, User user) {
        switch (user.getState()) {
            case DEFAULT -> {
                return new SendMessage(userId, WRONG_COMMAND_MESSAGE);
            }
            case WAIT_FOR_LINK_TO_ADD -> {
                if (isLinkCorrect(messageText)) {
                    Command command = commands.get(TRACK);
                    user.setState(State.DEFAULT);
                    return command.handle(update);
                }
            }
            case WAIT_FOR_LINK_TO_REMOVE -> {
                if (isLinkCorrect(messageText)) {
                    Command command = commands.get(UNTRACK);
                    user.setState(State.DEFAULT);
                    return command.handle(update);
                }
            }
        }
        return new SendMessage(userId, WRONG_LINK_MESSAGE);
    }

    public static boolean isLinkCorrect(String url) {
        if (url.startsWith("https://github.com/") || url.startsWith("https://stackoverflow.com/")) {
            try {
                URL linkUrl = new URL(url);
                linkUrl.toURI();
                HttpURLConnection connection = (HttpURLConnection) linkUrl.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == OK) {
                    return true;
                }
            } catch (MalformedURLException | URISyntaxException e) {
                return false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
