package com.personal.kafkatransactional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.personal.kafkatransactional.Data.Direction.IN;
import static com.personal.kafkatransactional.Data.Direction.OUT;

@Component
@RequiredArgsConstructor
public class TransactionalService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DataTableRepository dataTableRepository;

    @Value("${spring.kafka.template.default-topic}")
    private String topicName;

    @KafkaListener(topics = "${spring.kafka.template.default-topic}", groupId = "default")
    void listen(String message) {
        dataTableRepository.save(new Data(message, IN));
    }

    @Transactional("chainedTransactionManager")
    public void sendTransactionalMessage(String message, boolean isFailAfterSave, boolean isFailAfterSend) {
        dataTableRepository.save(new Data(message, OUT));

        if (isFailAfterSave) {
            throw new TransactionInterruptedException(message);
        }

        kafkaTemplate.send(topicName, message);

        if (isFailAfterSend) {
            throw new TransactionInterruptedException(message);
        }
    }

    static class TransactionInterruptedException extends RuntimeException {
        TransactionInterruptedException(String message) {
            super(message);
        }
    }
}
