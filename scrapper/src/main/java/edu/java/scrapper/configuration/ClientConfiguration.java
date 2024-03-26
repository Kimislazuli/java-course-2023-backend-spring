package edu.java.scrapper.configuration;

import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.client.GithubClient;
import edu.java.scrapper.client.StackOverflowClient;
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
    public BotClient botClient(WebClient.Builder builder, @Value("http://localhost:8090") String baseUrl) {
        log.info("Create Bot client bean with base url {}", baseUrl);
        return new BotClient(builder, baseUrl);
    }

    @Bean
    @Autowired
    public GithubClient githubClient(WebClient.Builder builder, @Value("${github.base-url}") String baseUrl) {
        log.info("Create GitHub client bean with base url {}", baseUrl);
        return new GithubClient(builder, baseUrl);
    }

    @Bean
    @Autowired
    public StackOverflowClient stackOverflowClient(
        WebClient.Builder builder,
        @Value("${stackoverflow.base-url}") String baseUrl
    ) {
        log.info("Create Stackoverflow client bean with base url {}", baseUrl);
        return new StackOverflowClient(builder, baseUrl);
    }
}
