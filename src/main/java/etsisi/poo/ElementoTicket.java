package etsisi.poo;

import java.util.ArrayList;
import java.util.List;

public class ElementoTicket<T extends TicketItem> {
    private final T item;
    private final int quantity;
    private final ArrayList<String> personalizados;

    public ElementoTicket(T item, int quantity, ArrayList<String> personalizados) {
        this.item = item;
        this.quantity = quantity;
        this.personalizados = personalizados;
    }

    public T getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public ArrayList<String> getPersonalizados() {
        return personalizados;
    }

}