package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.botLogic.commands.TrackCommand;
import edu.java.bot.botLogic.commands.UntrackCommand;
import edu.java.bot.model.Link;
import edu.java.bot.repository.LinkRepository;
import edu.java.bot.service.LinkService;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UntrackTest {
    private static final long CHAT_ID = 7L;

    private static final Update update = mock(Update.class);

    @BeforeAll
    static void setUpMock() {
        Message messageMock = mock(Message.class);
        Chat chatMock = mock(Chat.class);

        when(update.message()).thenReturn(messageMock);
        when(messageMock.chat()).thenReturn(chatMock);

        when(chatMock.id()).thenReturn(CHAT_ID);
        when(update.message().chat().id()).thenReturn(CHAT_ID);

        when(update.message().text()).thenReturn("link");
        when(messageMock.text()).thenReturn("link");
    }

    LinkRepository linkRepository = new LinkRepository();
    LinkService linkService = new LinkService(linkRepository);
    TrackCommand trackCommand = new TrackCommand(linkService);
    UntrackCommand untrackCommand = new UntrackCommand(linkService);

    @Test
    void successfullyRemoveLink() {
        trackCommand.handle(update);
        SendMessage handled = untrackCommand.handle(update);
        String actualResult = (String) handled.getParameters().get("text");
        List<Link> links = linkRepository.getUserLinks(7L);

        assertThat(links.size()).isEqualTo(0);
        assertThat(actualResult).startsWith("Ссылка ");
        assertThat(actualResult).endsWith(" успешно удалена.");
    }

    @Test
    void unremovableLink() {
        SendMessage handled = untrackCommand.handle(update);
        String actualResult = (String) handled.getParameters().get("text");

        assertThat(actualResult).startsWith("Ссылка ");
        assertThat(actualResult).endsWith(" не была ранее зарегистрирована.");
    }
}
