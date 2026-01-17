package etsisi.poo.products;

import java.time.Duration;

public enum EventType {
    FOOD(Duration.ofDays(3), "Food"),
    MEETING(Duration.ofHours(12), "Meeting");

    private final Duration minPlanning;
    private final String displayName;

    EventType(Duration minPlanning, String displayName) {
        this.minPlanning = minPlanning;
        this.displayName = displayName;
    }

    public Duration getMinPlanning() {
        return minPlanning;
    }

    public String getDisplayName() {
        return displayName;
    }
}
