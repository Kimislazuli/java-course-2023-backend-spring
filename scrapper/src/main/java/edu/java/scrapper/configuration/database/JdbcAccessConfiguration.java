package edu.java.scrapper.configuration.database;

import edu.java.scrapper.domain.dao.jdbc.JdbcChatDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.service.processing_services.LinkService;
import edu.java.scrapper.service.processing_services.TgChatService;
import edu.java.scrapper.service.processing_services.UpdaterService;
import edu.java.scrapper.service.processing_services.jdbc_impl.JdbcLinkService;
import edu.java.scrapper.service.processing_services.jdbc_impl.JdbcTgChatService;
import edu.java.scrapper.service.processing_services.jdbc_impl.JdbcUpdaterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    public TgChatService tgChatService(
        JdbcChatDao chatDao,
        JdbcLinkDao linkDao,
        JdbcChatToLinkConnectionDao connectionDao
    ) {
        return new JdbcTgChatService(linkDao, chatDao, connectionDao);
    }

    @Bean
    public LinkService linkService(
        JdbcChatDao chatDao,
        JdbcLinkDao linkDao,
        JdbcChatToLinkConnectionDao connectionDao
    ) {
        return new JdbcLinkService(linkDao, chatDao, connectionDao);
    }

    @Bean
    public UpdaterService updaterService(JdbcLinkDao linkDao) {
        return new JdbcUpdaterService(linkDao) {
        };
    }
}
