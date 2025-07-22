package ru.teachkfk.ConsumerApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaTopicListener.class);

    @KafkaListener(topics = "${kafka.topics.users}", groupId = "group_users")
    public void listenUsers(String data) {
        log.info("Listen User: {}", data);
    }

    @KafkaListener(topics = "${kafka.topics.orders}", groupId = "group_orders")
    public void listenOrders(String data) {
        log.info("Listen Order: {}", data);
    }

}
