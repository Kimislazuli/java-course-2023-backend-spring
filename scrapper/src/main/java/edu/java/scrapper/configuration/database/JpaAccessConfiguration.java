package edu.java.scrapper.configuration.database;

import edu.java.scrapper.domain.dao.jpa.JpaChatDao;
import edu.java.scrapper.domain.dao.jpa.JpaChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jpa.JpaLinkDao;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.UpdaterService;
import edu.java.scrapper.service.jpa_impl.JpaLinkService;
import edu.java.scrapper.service.jpa_impl.JpaTgChatService;
import edu.java.scrapper.service.jpa_impl.JpaUpdaterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Bean
    public TgChatService tgChatService(JpaChatDao chatDao) {
        return new JpaTgChatService(chatDao);
    }

    @Bean
    public LinkService linkService(JpaChatDao chatDao, JpaLinkDao linkDao, JpaChatToLinkConnectionDao connectionDao) {
        return new JpaLinkService(linkDao, chatDao, connectionDao);
    }

    @Bean
    public UpdaterService updaterService(JpaLinkDao linkDao) {
        return new JpaUpdaterService(linkDao) {
        };
    }
}
