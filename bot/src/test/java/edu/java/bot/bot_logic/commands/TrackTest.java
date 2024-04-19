package edu.java.bot.bot_logic.commands;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.service.LinkService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrackTest {
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

        when(chatMock.id()).thenReturn(7L);
        when(update.message().chat().id()).thenReturn(7L);

        when(update.message().text()).thenReturn("link");
        when(messageMock.text()).thenReturn("link");
    }

    ScrapperClient scrapperClient = new ScrapperClient(WebClient.builder(), "http://localhost:8080");
    LinkService linkService = new LinkService(scrapperClient);
    TrackCommand trackCommand = new TrackCommand(linkService);

    @Test
    void addLink() {
        stubFor(post(urlEqualTo("/links")).withHeader("Tg-Chat-Id", WireMock.equalTo("7"))
            .withRequestBody(equalToJson("""
                    {
                      "link": "link"
                    }""")).willReturn(aResponse().withStatus(200)));

        SendMessage handled = trackCommand.handle(update);
        String actualResult = (String) handled.getParameters().get("text");

        assertThat(actualResult).startsWith("Ссылка ");
        assertThat(actualResult).endsWith(" успешно добавлена.");
    }
}
