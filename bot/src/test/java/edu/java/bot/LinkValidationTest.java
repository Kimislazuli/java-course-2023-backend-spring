package edu.java.bot;

import edu.java.bot.botLogic.MessageHandler;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LinkValidationTest {
    @Test
    void nonLinkMessageTest() {
        boolean actualResult = MessageHandler.isLinkCorrect("aaa");

        assertThat(actualResult).isFalse();
    }

    @Test
    void linkWithSpaces() {
        boolean actualResult = MessageHandler.isLinkCorrect("https://github.com/ Kimislazuli/java-course-2023-backend-spring");

        assertThat(actualResult).isFalse();
    }

    @Test
    void brokenLink() {
        boolean actualResult = MessageHandler.isLinkCorrect("https://github.com/Kimislazuli/java-course-2023-backend-ng");

        assertThat(actualResult).isFalse();
    }

    @Test
    void wrongDomain() {
        boolean actualResult = MessageHandler.isLinkCorrect("https://google.com");

        assertThat(actualResult).isFalse();
    }

    @Test
    void correctLink() {
        boolean actualResult = MessageHandler.isLinkCorrect("https://github.com/Kimislazuli/java-course-2023-backend-spring");

        assertThat(actualResult).isTrue();
    }
}
