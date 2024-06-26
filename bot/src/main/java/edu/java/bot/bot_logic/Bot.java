package edu.java.bot.bot_logic;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandsScopeChat;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.bot_logic.commands.Command;
import edu.java.bot.configuration.ApplicationConfig;
import io.micrometer.core.instrument.Counter;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Bot implements AutoCloseable, UpdatesListener {
    private final ApplicationConfig config;
    private TelegramBot telegramBot;
    private final MessageHandler messageHandler;
    private final Counter processedMessagesCounter;
    private final List<? extends Command> commands;



    @Override
    public int process(List<Update> updates) {
        updates.forEach(
            update -> {
                if (update.message() != null) {
                    log.info("processing {}", update);
                    telegramBot.execute(messageHandler.handleRequest(update));
                    telegramBot.execute(createSetMyCommand()
                        .scope(new BotCommandsScopeChat(update.message().chat().id()))
                    );
                }
            }
        );

        processedMessagesCounter.increment();
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

    public void sendMessage(SendMessage message) {
        telegramBot.execute(message);
    }
}
