package edu.java.scrapper.configuration.database;

import edu.java.scrapper.domain.dao.jooq.JooqChatDao;
import edu.java.scrapper.domain.dao.jooq.JooqChatToLinkConnectionDao;
import edu.java.scrapper.domain.dao.jooq.JooqLinkDao;
import edu.java.scrapper.service.processing_services.LinkService;
import edu.java.scrapper.service.processing_services.TgChatService;
import edu.java.scrapper.service.processing_services.UpdaterService;
import edu.java.scrapper.service.processing_services.non_orm_impl.NonOrmLinkService;
import edu.java.scrapper.service.processing_services.non_orm_impl.NonOrmTgChatService;
import edu.java.scrapper.service.processing_services.non_orm_impl.NonOrmUpdaterService;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {
    @Bean
    public DefaultConfigurationCustomizer postgresJooqCustomizer() {
        return (DefaultConfiguration c) -> c.settings()
            .withRenderSchema(false)
            .withRenderFormatted(true)
            .withRenderQuotedNames(RenderQuotedNames.NEVER);
    }

    @Bean
    public TgChatService tgChatService(
        JooqChatDao chatDao,
        JooqLinkDao linkDao,
        JooqChatToLinkConnectionDao connectionDao
    ) {
        return new NonOrmTgChatService(linkDao, chatDao, connectionDao);
    }

    @Bean
    public LinkService linkService(
        JooqChatDao chatDao,
        JooqLinkDao linkDao,
        JooqChatToLinkConnectionDao connectionDao
    ) {
        return new NonOrmLinkService(linkDao, chatDao, connectionDao);
    }

    @Bean
    public UpdaterService updaterService(JooqLinkDao linkDao) {
        return new NonOrmUpdaterService(linkDao) {
        };
    }


}
