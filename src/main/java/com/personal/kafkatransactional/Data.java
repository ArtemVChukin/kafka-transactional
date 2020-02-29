package com.personal.kafkatransactional;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
public class Data {
    @Id
    @GeneratedValue
    private Long id;
    private String message;
    private Direction direction;

    public Data(String message, Direction direction) {
        this.message = message;
        this.direction = direction;
    }

    enum Direction {IN, OUT}
}
