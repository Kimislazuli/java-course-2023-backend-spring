package edu.java.bot.botLogic;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandsScopeChat;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.botLogic.commands.Command;
import edu.java.bot.configuration.ApplicationConfig;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Bot implements AutoCloseable, UpdatesListener {
    private ApplicationConfig config;
    private TelegramBot telegramBot;
    private MessageHandler messageHandler;
    private static final Logger LOGGER = LogManager.getLogger(Bot.class);

    private final List<? extends Command> commands;

    @Autowired
    public Bot(ApplicationConfig config, MessageHandler messageHandler, List<? extends Command> commands) {
        this.config = config;
        this.messageHandler = messageHandler;
        this.commands = commands;
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(
            update -> {
                if (update.message() != null) {
                    LOGGER.info("processing {}", update);
                    telegramBot.execute(messageHandler.handleRequest(update));
                    telegramBot.execute(createSetMyCommand().scope(new BotCommandsScopeChat(update.message().chat()
                        .id())));
                }
            }
        );

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @PostConstruct
    public void start() {
        telegramBot = new TelegramBot(config.telegramToken());
        telegramBot.execute(createSetMyCommand());
        telegramBot.setUpdatesListener(this);
    }

    private SetMyCommands createSetMyCommand() {
        BotCommand[] botCommandsArray = commands.stream()
            .map(
                command -> new BotCommand(
                    command.command(),
                    command.description()
                )
            ).toArray(BotCommand[]::new);
        return new SetMyCommands(botCommandsArray);
    }

    @Override
    public void close() {
        telegramBot.shutdown();
    }
}
