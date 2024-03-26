package edu.java.bot.configuration;

import edu.java.bot.client.ScrapperClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class ClientConfiguration {
    @Bean
    @Autowired
    public ScrapperClient scrapperClient(WebClient.Builder builder, @Value("http://localhost:8080") String baseUrl) {
        log.info("Create Scrapper client bean with base url {}", baseUrl);
        return new ScrapperClient(builder, baseUrl);
    }
}
