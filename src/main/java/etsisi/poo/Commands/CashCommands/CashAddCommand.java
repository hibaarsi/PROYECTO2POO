package etsisi.poo.Commands.CashCommands;

import etsisi.poo.Cashier;
import etsisi.poo.Commands.ICommand;
import etsisi.poo.UserController;
import etsisi.poo.errors.ValidationException;

public class CashAddCommand implements ICommand {
    private final UserController userController;

    public CashAddCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public String getPrimerArgumento() {
        return "cash";
    }

    @Override
    public String getSegundoArgumento() {
        return "add";
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 5) {

            String uw = args[2];
            String name = args[3].replace("\"", "");
            String email = args[4];

            Cashier cashier = userController.createCashier(name, email, uw);
            if (cashier == null) throw new ValidationException("Cashier could not be created");

            userController.addCashier(cashier);
            return cashier.toString() + "\ncash add: ok\n";

        } else if (args.length == 4) {

            String name = args[2].replace("\"", "");
            String email = args[3];
            Cashier cashier = userController.createCashier(name, email, null);
            if (cashier == null) throw new ValidationException("Cashier could not be created");

            userController.addCashier(cashier);
            return cashier.toString() + "\ncash add: ok\n";
        }

        return "cash add <UW> \"<name>\" <email>";
    }
}
