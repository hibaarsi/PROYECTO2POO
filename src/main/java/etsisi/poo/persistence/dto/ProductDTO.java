package etsisi.poo.persistence.dto;

public class ProductDTO {
    public String type; // "REGULAR" | "EVENT" | "PERSONALIZED"

    public int id;
    public String name;
    public double price;

    // REGULAR / PERSONALIZED
    public String category;  // Category enum name

    // EVENT
    public String eventDate; // LocalDateTime.toString()
    public int maxPeople;
    public String eventType; // EventType enum name

    // PERSONALIZED
    public int maxPersonal;
}
