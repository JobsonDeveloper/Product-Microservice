package br.com.product.micro.config;

import br.com.product.micro.event.dto.PaymentEventDto;
import br.com.product.micro.event.dto.SaleEventDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;


@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String kafkaUrl;

    @Value("${spring.kafka.consumer.group-id}")
    private String microserviceGroup;

    @Bean
    public ConsumerFactory<String, PaymentEventDto> paymentConsumerFactory() {

        JsonDeserializer<PaymentEventDto> deserializer =
                new JsonDeserializer<>(PaymentEventDto.class);

        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl,
                        ConsumerConfig.GROUP_ID_CONFIG, microserviceGroup
                ),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentEventDto> paymentKafkaListenerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, PaymentEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(paymentConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, SaleEventDto> saleConsumerFactory() {

        JsonDeserializer<SaleEventDto> deserializer =
                new JsonDeserializer<>(SaleEventDto.class);

        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl,
                        ConsumerConfig.GROUP_ID_CONFIG, microserviceGroup
                ),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SaleEventDto> saleKafkaListenerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, SaleEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(saleConsumerFactory());
        return factory;
    }
}
