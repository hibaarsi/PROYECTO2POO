package etsisi.poo.users;

import etsisi.poo.tickets.TicketItem;
import etsisi.poo.tickets.TicketModel;

import java.util.ArrayList;
import java.util.List;

public class Cashier extends Users {
    private String UW;
    private List<TicketModel<? extends TicketItem>> tickets;

    public Cashier(String name, String email, String UW) {
        super(name, email);
        this.UW = UW;
        this.tickets = new ArrayList<>();
    }

    public void addTicket(TicketModel ticket) {
        this.tickets.add(ticket);
    }

    public void removeTicket(TicketModel ticket) {
        this.tickets.remove(ticket);
    }

    public List<TicketModel<? extends TicketItem>> getTickets() {
        return this.tickets;
    }

    @Override
    public String getID() {
        return this.UW;
    }

    @Override
    public String toString() {
        return "Cash{identifier='" + UW + "', name='" + getName() + "', email='" + getEmail() + "'}";
    }
}
