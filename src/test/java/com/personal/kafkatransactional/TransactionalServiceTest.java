package com.personal.kafkatransactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static com.personal.kafkatransactional.Data.Direction.IN;
import static com.personal.kafkatransactional.Data.Direction.OUT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TransactionalServiceTest {
    @Autowired
    TransactionalService transactionalService;
    @Autowired
    DataTableRepository dataTableRepository;

    @Test
    void testTransactionalSuccess() {
        //given
        String sourceMessage = UUID.randomUUID().toString();

        //when
        transactionalService.sendTransactionalMessage(sourceMessage, false, false);

        //then
        Data data = dataTableRepository.getByMessageAndDirection(sourceMessage, OUT);
        assertEquals(sourceMessage, data.getMessage());
        data = dataTableRepository.getByMessageAndDirection(sourceMessage, IN);
        assertEquals(sourceMessage, data.getMessage());
        List<Data> results = dataTableRepository.getByMessage(sourceMessage);
        assertEquals(2, results.size());
    }

    @Test
    void testTransactionalFailAfterDb() {
        //given
        String sourceMessage = UUID.randomUUID().toString();

        //when
        TransactionalService.TransactionInterruptedException exception =
                assertThrows(TransactionalService.TransactionInterruptedException.class, () ->
                        transactionalService.sendTransactionalMessage(sourceMessage, true, false));

        //then
        assertEquals(sourceMessage, exception.getMessage());
        List<Data> results = dataTableRepository.getByMessage(sourceMessage);
        assertEquals(0, results.size());
    }

    @Test
    void testTransactionalFailAfterSave() {
        //given
        String sourceMessage = UUID.randomUUID().toString();

        //when
        TransactionalService.TransactionInterruptedException exception =
                assertThrows(TransactionalService.TransactionInterruptedException.class, () ->
                        transactionalService.sendTransactionalMessage(sourceMessage, false, true));

        //then
        assertEquals(sourceMessage, exception.getMessage());
        List<Data> results = dataTableRepository.getByMessage(sourceMessage);
        assertEquals(0, results.size());
    }
}
