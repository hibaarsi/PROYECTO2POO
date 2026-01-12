package etsisi.poo;

import java.time.*;

public class EventProducts extends Product implements IEventProduct {

    private final LocalDateTime eventDate;
    private final int maxPeople;
    private final EventType eventType;
    private int actualPeople = 0;

    public EventProducts(
            int id,
            String name,
            double price,
            LocalDateTime eventDate,
            int maxPeople,
            EventType eventType
    ) {
        super(id, name, price);
        this.eventDate = eventDate;
        this.maxPeople = maxPeople;
        this.eventType = eventType;

        if (maxPeople < 1 || maxPeople > 100 || !hasEnoughPlanning()) {
            throw new IllegalArgumentException("Error adding product\n");
        }
    }

    @Override
    public LocalDateTime getEventDate() {
        return eventDate;
    }

    @Override
    public int getMaxPeople() {
        return maxPeople;
    }

    @Override
    public Duration getMinPlanning() {
        return eventType.getMinPlanning();
    }

    public void setActualPeople(int n) {
        this.actualPeople = n;
    }

    public int getActualPeople() {
        return actualPeople;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        if (actualPeople > 0) {
            return String.format(
                    java.util.Locale.US,
                    "{class:%s, id:%d, name:'%s', price:%.1f, date of Event:%s, max people allowed:%d, actual people in event:%d}",
                    eventType.getDisplayName(),
                    id,
                    name.replace("\"", ""),
                    price,
                    eventDate.toLocalDate(),
                    maxPeople,
                    actualPeople
            );
        }

        return String.format(
                java.util.Locale.US,
                "{class:%s, id:%d, name:'%s', price:%.1f, date of Event:%s, max people allowed:%d}",
                eventType.getDisplayName(),
                id,
                name.replace("\"", ""),
                price,
                eventDate.toLocalDate(),
                maxPeople
        );
    }
}
