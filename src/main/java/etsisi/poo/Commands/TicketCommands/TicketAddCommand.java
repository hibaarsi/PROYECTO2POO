package etsisi.poo.Commands.TicketCommands;

import etsisi.poo.*;
import etsisi.poo.Commands.ICommand;

import java.util.ArrayList;
import java.util.List;
//REVISAR
public class TicketAddCommand implements ICommand {
    private final TicketController ticketController;
    private final UserController userController;
    private final Catalog catalog;

    public TicketAddCommand(TicketController ticketController, UserController userController, Catalog catalog) {
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
        return "add";
    }

    @Override
    public String execute(String[] args) {
        if (args.length < 5) {
            return "Usage:  ticket add <ticketId><cashId> <prodId> <amount> [--p<txt> --p<txt>]";
        }
        try {
            String ticketId = args[2];
            String cashierId = args[3];
            String itemId = args[4]; //para que vaya con 1S

            TicketModel<? extends TicketItem> ticket = ticketController.getTicket(ticketId);
            if (ticket == null) return "Ticket not found";

            Cashier cashier = userController.getCashier(cashierId);
            if (cashier == null) return "Cashier not found";

            if (!ticketController.cashierHasTicket(cashierId, ticket)) {
                return "Ticket does not belong to cashier";
            }

            // cantidad por defecto = 1 (para servicios o si no se indica)
            int quantity = 1;
            int i = 5;

            // Si hay cantidad y no es un flag de personalización
            if (args.length > 5 && !args[5].startsWith("--p")) {
                quantity = Integer.parseInt(args[5]);
                if (quantity <= 0) return "Invalid amount";
                i = 6;
            }

            // personalizaciones
            ArrayList<String> personalizations = new ArrayList<>();
            for (; i < args.length; i++) {
                if (args[i].startsWith("--p")) {
                    String p = args[i].substring(3);
                    if (!p.isEmpty()) personalizations.add(p);
                }
            }

            // Resolver item
            TicketItem item;
            if (itemId.endsWith("S")) {
                item = catalog.getService(itemId);
                if (item == null) return "Service not found";
            } else {
                int prodId = Integer.parseInt(itemId);
                item = catalog.getProduct(prodId);
                if (item == null) return "Product not found";
            }

            boolean ok = ticketController.addItemToTicket(ticketId, item, quantity, personalizations);
            if (!ok) return "Item not added";


            ticket.print();

            return "ticket add: ok\n";

        } catch (NumberFormatException e) {
            return "Invalid number format";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}