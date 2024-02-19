package edu.java.bot;

import edu.java.bot.botLogic.MessageHandler;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LinkValidationTest {
    private MessageHandler messageHandler = new MessageHandler();

    @Test
    void nonLinkMessageTest() {
        boolean actualResult = messageHandler.isLinkCorrect("aaa");

        assertThat(actualResult).isFalse();
    }

    @Test
    void linkWithSpaces() {
        boolean actualResult = messageHandler.isLinkCorrect("https://github.com/ Kimislazuli/java-course-2023-backend-spring");

        assertThat(actualResult).isFalse();
    }

    @Test
    void brokenLink() {
        boolean actualResult = messageHandler.isLinkCorrect("https://github.com/Kimislazuli/java-course-2023-backend-ng");

        assertThat(actualResult).isFalse();
    }

    @Test
    void wrongDomain() {
        boolean actualResult = messageHandler.isLinkCorrect("https://google.com");

        assertThat(actualResult).isFalse();
    }

    @Test
    void correctLink() {
        boolean actualResult = messageHandler.isLinkCorrect("https://github.com/Kimislazuli/java-course-2023-backend-spring");

        assertThat(actualResult).isTrue();
    }
}
