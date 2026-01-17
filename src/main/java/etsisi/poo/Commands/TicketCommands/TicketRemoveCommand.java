package etsisi.poo.Commands.TicketCommands;

import etsisi.poo.Commands.ICommand;
import etsisi.poo.errors.ValidationException;
import etsisi.poo.products.Catalog;
import etsisi.poo.products.Product;
import etsisi.poo.tickets.TicketController;
import etsisi.poo.tickets.TicketModel;
import etsisi.poo.users.Cashier;
import etsisi.poo.users.UserController;

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
            throw new ValidationException("Usage: ticket remove <ticketId> <cashierId> <productId>");
        }
        String ticketId = args[2];
        String cashierId = args[3];
        int productId = Integer.parseInt(args[4]);
        //TicketModel ticket = ticketController.getTicket(ticketId);
        TicketModel<?> ticket = ticketController.getTicketByBaseOrFullId(ticketId);

        if (ticket == null) {
            throw new ValidationException("Ticket ID not found");
        }
        Cashier cashier = userController.getCashier(cashierId);
        if (cashier == null) {
            throw new ValidationException("Cashier ID not found");
        }
        if (!ticketController.cashierHasTicket(cashierId, ticket)) {
            throw new ValidationException("This ticket does not belong to cashier " + cashierId);
        }
        Product product = catalog.getProduct(productId);
        if (product == null) {
            throw new ValidationException("Product ID not found");
        }
        boolean remove=ticketController.removeProductFromTicket(ticketId,product);
        if(!remove) return "Product not removed";
      //  ticketController.printTicketInfo(ticket);
        ticket.print();
        return "ticket remove: ok\n";

    }
}
