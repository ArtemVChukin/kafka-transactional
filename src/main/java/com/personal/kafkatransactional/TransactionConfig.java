package com.personal.kafkatransactional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;

import static org.springframework.transaction.support.AbstractPlatformTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION;

@EnableKafka
@Configuration
public class TransactionConfig {

    @Primary
    @Bean("transactionManager")
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    public KafkaTransactionManager kafkaTransactionManager(ProducerFactory producerFactory) {
        KafkaTransactionManager kafkaTransactionManager = new KafkaTransactionManager(producerFactory);
        kafkaTransactionManager.setTransactionSynchronization(SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        return kafkaTransactionManager;
    }

    @Bean
    @SuppressWarnings("rawtypes")
    public ChainedTransactionManager chainedTransactionManager(JpaTransactionManager jpaTransactionManager,
                                                               KafkaTransactionManager kafkaTransactionManager) {
        return new ChainedTransactionManager(kafkaTransactionManager, jpaTransactionManager);
    }
}
