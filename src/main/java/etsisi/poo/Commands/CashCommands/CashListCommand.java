package etsisi.poo.Commands.CashCommands;

import etsisi.poo.Commands.ICommand;
import etsisi.poo.UserController;
import etsisi.poo.errors.ValidationException;

public class CashListCommand implements ICommand {
    private final UserController userController;

    public CashListCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public String getPrimerArgumento() {
        return "cash";
    }

    @Override
    public String getSegundoArgumento() {
        return "list";
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 2) {
            throw new ValidationException("Use: cash list");
        }
        //userController.listCashier();
        return userController.listCashier() + "cash list: ok\n";
    }
}
