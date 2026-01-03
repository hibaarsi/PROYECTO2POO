package etsisi.poo.Commands.ProdCommands;

import etsisi.poo.Catalog;
import etsisi.poo.Commands.ICommand;
import etsisi.poo.Product;

public class ProdRemoveCommand implements ICommand {
    private final Catalog catalog;

    public ProdRemoveCommand(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public String getPrimerArgumento() {
        return "prod";
    }

    @Override
    public String getSegundoArgumento() {
        return "remove";
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 3) {
            return "prod remove <id>";
        }
        try {
            int id = Integer.parseInt(args[2]);
            Product removed = catalog.removeProduct(id); //intenta eliminar

            if (removed == null) {
                return "Product with id " + id + " does not exist";
            }

            return removed + "\nprod remove: ok\n";

        } catch (NumberFormatException e) {
            return "Invalid ID format";
        }

    }
}
