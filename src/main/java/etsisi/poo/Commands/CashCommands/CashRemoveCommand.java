package etsisi.poo.Commands.CashCommands;

import etsisi.poo.users.Cashier;
import etsisi.poo.Commands.ICommand;
import etsisi.poo.tickets.TicketController;
import etsisi.poo.users.UserController;
import etsisi.poo.errors.ValidationException;

public class CashRemoveCommand implements ICommand {
    private final UserController userController;
    private final TicketController ticketController;

    public CashRemoveCommand(UserController userController, TicketController ticketController) {
        this.userController = userController;
        this.ticketController = ticketController;
    }

    @Override
    public String getPrimerArgumento() {
        return "cash";
    }

    @Override
    public String getSegundoArgumento() {
        return "remove";
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 3) {
            throw new ValidationException("Usage: cash remove <id>");
        }
        String cashierId = args[2];
        Cashier cashier = userController.getCashier(cashierId);

        if (cashier == null) {
            throw new ValidationException("Cajero no encontrado");
        }

        ticketController.removeTicketsFromCashier(cashier);
        userController.removeCashier(cashierId);
        return "cash remove: ok\n";
    }

}
