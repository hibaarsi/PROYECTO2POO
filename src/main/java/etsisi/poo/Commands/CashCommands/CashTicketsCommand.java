package etsisi.poo.Commands.CashCommands;

import etsisi.poo.users.Cashier;
import etsisi.poo.Commands.ICommand;
import etsisi.poo.tickets.TicketModel;
import etsisi.poo.users.UserController;
import etsisi.poo.errors.ValidationException;

public class CashTicketsCommand implements ICommand {
    private final UserController userController;

    public CashTicketsCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public String getPrimerArgumento() {
        return "cash";
    }

    @Override
    public String getSegundoArgumento() {
        return "tickets";
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 3) {
            throw new ValidationException("Usage: cash tickets <UW>");
        }
        String uw = args[2];
        Cashier cashier = userController.getCashier(uw);

        if (cashier == null) {
            throw new ValidationException("Cashier not found");
        }

        StringBuilder result = new StringBuilder();
        result.append("Tickets:\n");

        if (cashier.getTickets().isEmpty()) {
            return result.append("cash tickets: ok\n").toString();
        }

        for (TicketModel t : cashier.getTickets()) {
            result.append("  ")
                    .append(t.getId())
                    .append("->")
                    .append(t.getTicketStatus())
                    .append("\n");
        }

        result.append("cash tickets: ok\n");
        return result.toString();

        /*
        System.out.println("Tickets: ");
        if (cashier.getTickets().isEmpty()) {
            return "cash tickets: ok\n";
        }
        for (TicketModel t : cashier.getTickets()) {
            System.out.println("  " + t.getId() + "->" + t.getTicketStatus());
        }

        return "cash tickets: ok\n";*/
    }
}
