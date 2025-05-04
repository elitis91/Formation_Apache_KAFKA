package com.orsys.kafkaintegration.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

	@KafkaListener(topics = "myorsystopic", groupId= "my-group-id")
	public void listen(String message) {
		System.out.println("Received message :" + message);
	}
}
