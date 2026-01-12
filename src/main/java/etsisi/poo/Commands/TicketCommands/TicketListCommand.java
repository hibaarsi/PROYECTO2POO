package etsisi.poo.Commands.TicketCommands;

import etsisi.poo.Commands.ICommand;
import etsisi.poo.TicketController;
import etsisi.poo.TicketModel;
import etsisi.poo.errors.ValidationException;

import java.util.List;

public class TicketListCommand implements ICommand {
    private final TicketController ticketController;

    public TicketListCommand(TicketController ticketController) {
        this.ticketController = ticketController;
    }
    @Override
    public String getPrimerArgumento() {
        return "ticket";
    }
    @Override
    public String getSegundoArgumento() {
        return "list";
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 2){
            throw new ValidationException("Usage: ticket list");
        }
        ticketController.listAllTickets();
        return "ticket list: ok\n";
    }




}
