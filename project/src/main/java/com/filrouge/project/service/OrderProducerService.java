package com.filrouge.project.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.filrouge.project.models.Order;
import com.github.javafaker.Faker;

@Service
public class OrderProducerService {

  @Autowired
  private KafkaTemplate<String, Order> kafkaTemplate;
  
  private final Faker faker = new Faker();
  
  private String topic = "projectTopic";
  
  @Scheduled(fixedRateString = "50")
  public void emitRandomOrder() {
    String product = faker.commerce().productName();      // e.g. "Rustic Lamp"
    int qty        = faker.number().numberBetween(1, 5);
    double price = faker.number().randomDouble(2, 10, 250);   // 2 décimales, entre 10 et 250
    String orderId = UUID.randomUUID().toString();

    Order order = new Order(orderId, product, qty, price * qty);
    kafkaTemplate.send(topic, product, order);
    
    order.toString();
  }
}
