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
        if (args.length < 6) {
            return "Usage: ticket add <ticketId> <cashierId> <productId> <quantity> [--p personalization1 --p personalization2 ...]";
        }
        try {
            String ticketId = args[2];
            String cashierId = args[3];
            int productId = Integer.parseInt(args[4]);
            int quantity = Integer.parseInt(args[5]);

            ArrayList<String> personalizations = new ArrayList<>();
            for (int i = 6; i < args.length; i++) {
                if (args[i].startsWith("--p")) {
                    String personalization = args[i].substring(3); // despues de --p empieza la personalizacion
                    if (!personalization.isEmpty()) {
                        personalizations.add(personalization);
                    }
                }
            }

           TicketItem product = catalog.getProduct(productId);
            if (product == null) {
                return "Product not found";
            }

            TicketModel<? extends TicketItem> ticket = ticketController.getTicket(ticketId);
            if (ticket == null) return "Ticket not found";
            Cashier cashier = userController.getCashier(cashierId);
            if (cashier == null) return "Cashier not found";

            if (product instanceof EventProducts) {
                for (ElementoTicket elementoTicket : ticket.getElementos()) {
                    if ( elementoTicket.getItem().equals(product.getId())) //mirar
                        return "Product already in ticket";

                }
            }
           boolean add= ticketController.addItemToTicket(ticketId, product, quantity, personalizations);
            if(!add) return "Product not added";
           // ticketController.printTicketInfo(ticket);
        } catch (NumberFormatException e) {
            return "Invalid number format for product ID or quantity";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return "ticket add: ok\n";
    }

}