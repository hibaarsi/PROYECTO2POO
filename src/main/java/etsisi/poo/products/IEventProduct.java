package etsisi.poo.products;

import java.time.Duration;
import java.time.LocalDateTime;

public interface IEventProduct {
    LocalDateTime getEventDate();

    int getMaxPeople();

    Duration getMinPlanning(); //tiempo mínimo de planificación

    default boolean hasEnoughPlanning() {
        return Duration.between(LocalDateTime.now(), getEventDate()).compareTo(getMinPlanning()) >= 0;
    }
}