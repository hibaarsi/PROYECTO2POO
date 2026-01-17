package etsisi.poo.Commands.ClientCommands;

import etsisi.poo.users.Cashier;
import etsisi.poo.users.Client;
import etsisi.poo.Commands.ICommand;
import etsisi.poo.users.UserController;
import etsisi.poo.errors.ValidationException;

public class ClientAddCommand implements ICommand {
    private final UserController userController;

    public ClientAddCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public String getPrimerArgumento() {
        return "client";
    }

    @Override
    public String getSegundoArgumento() {
        return "add";
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 6) {
            throw new ValidationException("Use: client add \"<name>\" <DNI|NIF> <email> <UW_cashier>");
        }
        String name = args[2].replace("\"", "");
        String id= args[3];
        String email = args[4];
        String uw = args[5];

        Cashier cashier = userController.getCashier(uw);
        if (cashier == null) {
            throw new ValidationException ("Cashier not found");
        }

        Client client = userController.createClient(name, email, id, cashier);

        //if (client == null) {
        //    throw new ValidationException("Client could not be created.");
        //}

        userController.addClient(client);
       // System.out.println(client);
        //return "client add: ok\n";
        return client.toString() + "\nclient add: ok\n";
    }
}
