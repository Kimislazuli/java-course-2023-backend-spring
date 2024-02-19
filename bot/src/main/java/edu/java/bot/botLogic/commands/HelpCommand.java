package edu.java.bot.botLogic.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component("help")
public class HelpCommand implements Command {
    private Set<? extends Command> commandSet = new HashSet<>();

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String description() {
        return "список доступных команд";
    }

    @Override
    public SendMessage handle(Update update) {
        return new SendMessage(update.message().chat().id(), generateHelpMessage());
    }

    private String generateHelpMessage() {
        return String.join(
            "\n",
            commandSet.stream().map(command -> String.format("%s - %s", command.command(), command.description()))
                .toList()
        );
    }

    public void setCommands(Set<? extends Command> commands) {
        commandSet = commands;
    }
}
