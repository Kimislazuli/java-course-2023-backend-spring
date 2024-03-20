package edu.java.bot.bot_logic.commands;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.model.Link;
import edu.java.bot.repository.LinkRepository;
import edu.java.bot.service.LinkService;
import edu.java.models.dto.response.LinkResponse;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrackTest {
    private static final long CHAT_ID = 7L;

    private static final Update update = mock(Update.class);

    private static final WireMockServer server = new WireMockServer();

    @AfterAll
    public static void tearDown() {
        server.stop();
    }

    @BeforeAll
    static void setUpMock() {
        server.start();

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
    ScrapperClient scrapperClient = new ScrapperClient(WebClient.builder(), "http://localhost:8080");
    LinkService linkService = new LinkService(linkRepository, scrapperClient);
    TrackCommand trackCommand = new TrackCommand(linkService);

    @Test
    void addLink() {
        String link = "https://github.com/Kimislazuli/java-course-2023-backend-spring";
        LinkResponse actualResponse = new LinkResponse(CHAT_ID, URI.create(link));

        when(scrapperClient.addLink(CHAT_ID, link)).thenReturn(Optional.of(actualResponse));

        SendMessage handled = trackCommand.handle(update);
        String actualResult = (String) handled.getParameters().get("text");
        List<Link> links = linkRepository.getUserLinks(7L);

        assertThat(links.getFirst().url()).isEqualTo(link);
        assertThat(actualResult).startsWith("Ссылка ");
        assertThat(actualResult).endsWith(" успешно добавлена.");
    }
}
