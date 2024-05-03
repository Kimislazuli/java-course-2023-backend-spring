package edu.java.bot.service;

import edu.java.models.dto.LinkUpdate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.java.models.dto.UpdateType;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.serializer.JsonSerializer;

@SpringBootTest
@RequiredArgsConstructor
public class KafkaTest extends KafkaIntegrationEnvironment {
    @Value("${app.topic.name}")
    private String topic;
    @MockBean
    private BotService botService;

    @Test
    public void correctMessageTest() {
        try (KafkaProducer<String, LinkUpdate> kafkaProducer = new KafkaProducer<>(getProducerProps())) {
            kafkaProducer.send(new ProducerRecord<>(topic, new LinkUpdate(1L, "www.url.com", "link", List.of(1L), UpdateType.DEFAULT)));

            Mockito.verify(botService, Mockito.after(1000).times(1))
                .sendUpdatesInfo(new LinkUpdate(1L, "www.url.com", "link", List.of(1L), UpdateType.DEFAULT));
        }
    }

    private Map<String, Object> getProducerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }
}
