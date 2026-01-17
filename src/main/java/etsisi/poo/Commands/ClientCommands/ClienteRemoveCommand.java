package etsisi.poo.Commands.ClientCommands;

import etsisi.poo.users.Client;
import etsisi.poo.Commands.ICommand;
import etsisi.poo.users.UserController;
import etsisi.poo.errors.ValidationException;

public class ClienteRemoveCommand implements ICommand {
    private final UserController userController;

    public ClienteRemoveCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public String getPrimerArgumento() {
        return "client";
    }

    @Override
    public String getSegundoArgumento() {
        return "remove";
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 3) {
            throw new ValidationException("Use: client remove <DNI>");
        }
        String dni = args[2];
        Client client = userController.getClient(dni);

        if (client == null) {
            throw new ValidationException("Client does not exist.");
        }

        userController.removeClient(dni);
        return "client remove: ok\n";

    }
}
