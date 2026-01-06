package etsisi.poo.persistence.dto;

import java.util.ArrayList;
import java.util.List;

public class TicketItemDTO {
    public String itemType;
    public String itemId;
    public int quantity;
    public List<String> personalizados = new ArrayList<>();
}
