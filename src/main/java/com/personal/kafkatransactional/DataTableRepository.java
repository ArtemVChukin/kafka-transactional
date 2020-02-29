package com.personal.kafkatransactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataTableRepository extends CrudRepository<Data, String> {
    Data getByMessageAndDirection(String message, Data.Direction direction);

    List<Data> getByMessage(String message);
}
