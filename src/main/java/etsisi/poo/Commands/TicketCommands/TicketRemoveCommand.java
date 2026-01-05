package etsisi.poo.Commands.TicketCommands;

import etsisi.poo.*;
import etsisi.poo.Commands.ICommand;

public class TicketRemoveCommand implements ICommand {
    private final TicketController ticketController;
    private final UserController userController;
    private final Catalog catalog;
    public TicketRemoveCommand(TicketController ticketController, UserController userController, Catalog catalog) {
        this.ticketController = ticketController;
        this.userController = userController;
        this.catalog = catalog;
    }

    @Override
    public String getPrimerArgumento() {
        return "ticket";
    }

    @Override
    public String getSegundoArgumento() {
        return "remove";
    }

    @Override
    public String execute(String[] args) {
        if (args.length < 5) {
            return "Usage: ticket remove <ticketId> <cashierId> <productId>";
        }
        String ticketId = args[2];
        String cashierId = args[3];
        int productId = Integer.parseInt(args[4]);
        TicketModel ticket = ticketController.getTicket(ticketId);
        if (ticket == null) {
            return "Ticket ID not found";
        }
        Cashier cashier = userController.getCashier(cashierId);
        if (cashier == null) {
            return "Cashier ID not found";
        }
        if (!ticketController.cashierHasTicket(cashierId, ticket)) {
            return "This ticket does not belong to cashier " + cashierId;
        }
        Product product = catalog.getProduct(productId);
        if (product == null) {
            return "Product ID not found";
        }
        boolean remove=ticketController.removeProductFromTicket(ticketId,product);
        if(!remove) return "Product not removed";
      //  ticketController.printTicketInfo(ticket);
        return "ticket remove: ok\n";

    }
}
