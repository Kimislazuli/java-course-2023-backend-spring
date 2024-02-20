package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.botLogic.commands.TrackCommand;
import edu.java.bot.model.Link;
import edu.java.bot.repository.LinkRepository;
import edu.java.bot.service.LinkService;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrackTest {
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

    @Test
    void addLink() {
        SendMessage handled = trackCommand.handle(update);
        String actualResult = (String) handled.getParameters().get("text");
        List<Link> links = linkRepository.getUserLinks(7L);

        assertThat(links.getFirst().getUrl()).isEqualTo("link");
        assertThat(actualResult).startsWith("Ссылка ");
        assertThat(actualResult).endsWith(" успешно добавлена.");
    }
}
