package com.filrouge.project;

import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.filrouge.project.models.Order;
import com.filrouge.project.models.ProductRevenue;

@SpringBootApplication
@EnableScheduling 
public class ProjectApplication {
	
	

	
    private static final String INPUT_TOPIC  = "projectTopic";
    private static final String OUTPUT_TOPIC = "revenue";

	public static void main(String[] args) {
		
		SpringApplication.run(ProjectApplication.class, args);
		
		 /* 1 . Configuration Streams */
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "order-revenue-app-" + UUID.randomUUID());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,org.springframework.kafka.support.serializer.JsonSerde.class);
        
        props.put("spring.json.value.default.type", "com.filrouge.project.models.ProductRevenue");
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
		
        /* 2 . Serdes */
        JsonSerde<Order>          orderSerde   = new JsonSerde<>(Order.class);
        JsonSerde<ProductRevenue> revenueSerde = new JsonSerde<>(ProductRevenue.class);
		
        /* 3 . Topology */
        StreamsBuilder builder = new StreamsBuilder();

        // Source: commandes
        KStream<String, Order> orders = builder.stream(
                INPUT_TOPIC,
                Consumed.with(Serdes.String(), orderSerde));

        // Agrégation CA par produit, fenêtre tumbling 1 min
        TimeWindows window = TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1));
        
        KTable<Windowed<String>, Double> revenueTable = orders
                .groupBy((key, order) -> order.getProductName(),
                         Grouped.with(Serdes.String(), orderSerde))
                .windowedBy(window)
                .aggregate(
                        () -> 0.0,                                   // initial
                        (product, order, agg) -> agg + order.getTotalPrice(),
                        Materialized.<String, Double, WindowStore<Bytes, byte[]>>as("revenue-store")
                                   .withKeySerde(Serdes.String())
                                   .withValueSerde(Serdes.Double())
                );
        
        // Format de sortie et envoi dans orders.revenue
        revenueTable
                .toStream()
                .mapValues((windowedKey, total) -> new ProductRevenue(
                        windowedKey.window().start(),
                        windowedKey.window().end(),
                        windowedKey.key(),
                        Math.round(total * 100.0) / 100.0           // 2 décimales
                ))
                .to(OUTPUT_TOPIC);

        /* 4 . Lancement */
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // Fermeture propre sur SIGTERM
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));


	}

}
