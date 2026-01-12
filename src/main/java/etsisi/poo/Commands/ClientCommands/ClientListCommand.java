package etsisi.poo.Commands.ClientCommands;

import etsisi.poo.Commands.ICommand;
import etsisi.poo.UserController;
import etsisi.poo.errors.ValidationException;

public class ClientListCommand implements ICommand {
    private final UserController userController;

    public ClientListCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public String getPrimerArgumento() {
        return "client";
    }

    @Override
    public String getSegundoArgumento() {
        return "list";
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 2) {
            throw new ValidationException("Use: client list");
        }

        userController.listClients();
        return "client list: ok\n";
    }
}
