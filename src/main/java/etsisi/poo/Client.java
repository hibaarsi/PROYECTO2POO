package etsisi.poo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Client extends Users {
    protected String id;
    protected Cashier cashier;
    protected List<TicketModel> tickets;

    public Client(String name, String email, String id, Cashier cashier) {
        super(name, email);
        this.id= id;
        this.cashier = cashier;
        this.tickets = new ArrayList<>();
    }

    public void addTicket(TicketModel ticket) {
        this.tickets.add(ticket);
    }

    public void removeTicket(TicketModel ticket) {
        this.tickets.remove(ticket);
    }

    public List<TicketModel> getTickets(){return this.tickets;}

    public Cashier getCashier() {
        return this.cashier;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public String toString() {
        return String.format(
                "USER{identifier='%s', name='%s', email='%s', cash=%s}",
                this.id, getName(), getEmail(), this.cashier.getID());
    }
}
