package edu.java.bot.bot_logic;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot_logic.commands.Command;
import edu.java.bot.bot_logic.commands.HelpCommand;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.model.Chat;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final ScrapperClient scrapperClient;
    private final Map<String, Command> commands;

    @Autowired
    public MessageHandler(ScrapperClient scrapperClient, Set<? extends Command> commandsSet) {
        this.scrapperClient = scrapperClient;
        commands = commandsSet.stream().collect(Collectors.toMap(Command::command, Function.identity()));
        HelpCommand help = (HelpCommand) commands.get(HELP);
        help.setCommands(commandsSet);
    }

    public SendMessage handleRequest(Update update) {
        String messageText = update.message().text();
        Long userId = update.message().chat().id();
        Optional<Chat> chat = scrapperClient.getChat(userId);
        if (commands.containsKey(messageText)) {
            return handleCommandInput(update, messageText, userId, chat);
        } else if (chat.isPresent()) {
            return handleOtherInput(update, messageText, userId, chat.get());
        }
        return new SendMessage(userId, WRONG_COMMAND_MESSAGE);
    }

    private SendMessage handleCommandInput(Update update, String messageText, Long userId, Optional<Chat> chat) {
        Command command = commands.get(messageText);
        if (chat.isEmpty() || command.command().equals(LIST)) {
            if (List.of(HELP, START).contains(messageText) || (command.command().equals(LIST) && chat.isPresent())) {
                return command.handle(update);
            }
            return new SendMessage(userId, NOT_AVAILABLE_COMMAND_MESSAGE);
        }
        Chat extractedChat = chat.get();
        switch (messageText) {
            case START -> {
                return new SendMessage(userId, "Вы уже авторизованы");
            }
            case TRACK -> {
                extractedChat.setState(State.WAIT_FOR_LINK_TO_ADD);
                scrapperClient.updateChatState(extractedChat.getTelegramId(), extractedChat.getState().ordinal());
            }

            case UNTRACK -> {
                extractedChat.setState(State.WAIT_FOR_LINK_TO_REMOVE);
                scrapperClient.updateChatState(extractedChat.getTelegramId(), extractedChat.getState().ordinal());
            }
        }
        return new SendMessage(userId, "Отправьте ссылку");

    }

    private SendMessage handleOtherInput(Update update, String messageText, Long userId, Chat chat) {
        log.info(String.valueOf(chat.getState()));
        switch (chat.getState()) {
            case DEFAULT -> {
                return new SendMessage(userId, WRONG_COMMAND_MESSAGE);
            }
            case WAIT_FOR_LINK_TO_ADD -> {
                if (isLinkCorrect(messageText)) {
                    Command command = commands.get(TRACK);
                    chat.setState(State.DEFAULT);
                    scrapperClient.updateChatState(chat.getTelegramId(), chat.getState().ordinal());
                    return command.handle(update);
                }
            }
            case WAIT_FOR_LINK_TO_REMOVE -> {
                if (isLinkCorrect(messageText)) {
                    Command command = commands.get(UNTRACK);
                    chat.setState(State.DEFAULT);
                    scrapperClient.updateChatState(chat.getTelegramId(), chat.getState().ordinal());
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
