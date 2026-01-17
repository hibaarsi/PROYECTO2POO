package etsisi.poo.Commands.ProdCommands;

import etsisi.poo.products.Catalog;
import etsisi.poo.Commands.ICommand;
import etsisi.poo.products.Product;
import etsisi.poo.errors.ValidationException;

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
        /*if (args.length != 3) {
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
        }*/
        // 1. Validar argumentos
        if (args.length != 3) {
            throw new ValidationException("Usage: prod remove <id>");
        }

        // 2. Parsear ID (con manejo de error)
        int id;
        try {
            id = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid ID format: " + args[2]);
        }

        // 3. Intentar eliminar
        Product removed = catalog.removeProduct(id);

        if (removed == null) {
            // CAMBIO: Lanzar excepción en lugar de devolver string plano
            throw new ValidationException("Product with id " + id + " does not exist");
        }

        return removed + "\nprod remove: ok\n";


    }
}
