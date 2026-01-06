package etsisi.poo.persistence.dto;

import java.util.ArrayList;
import java.util.List;

public class TicketDTO {
    //EXPLICACION: Un DTO(Data Transfer Object) es una clase simple que solo tiene datos
    // Sirve para guardar o transportar información sin arrastrar toda la complejidad del
    // modelo real (subclases, referencias cruzadas, printers, etc.).
    // Evita problemas al serializar: ciclos, clases abstractas, objetos enormes.

    // TicketModel es abstracto y tiene relaciones con TicketItem, ElementoTicket, Client, Cashier

    public String id;
    public String status;     // EMPTY/OPEN/CLOSED
    public String openDate;
    public String endDate;

    public String tipo;       // p/m/s
    public String cashierId;  // UW
    public String clientId;   // DNI

    public List<TicketItemDTO> items = new ArrayList<>();
}
