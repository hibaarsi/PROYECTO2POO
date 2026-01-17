package etsisi.poo.Commands.ProdCommands;

import etsisi.poo.products.Catalog;
import etsisi.poo.Commands.ICommand;
import etsisi.poo.products.Product;
import etsisi.poo.errors.ValidationException;

public class ProdUpdateCommand implements ICommand {
    private final Catalog catalog;

    public ProdUpdateCommand(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public String getPrimerArgumento() {
        return "prod";
    }

    @Override
    public String getSegundoArgumento() {
        return "update";
    }


    @Override
    public String execute(String[] args) {
        //try {
            // 1. Validar longitud
            if (args.length != 5) {
                throw new ValidationException("Usage: prod update <id> NAME|CATEGORY|PRICE <value>");
            }

            // 2. Parsear ID
            int id;
            try {
                id = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid ID format: " + args[2]);
            }

            String field = args[3].toUpperCase();
            String value = args[4].replace("\"", "");


            catalog.updateProduct(id, field, value);

            // 4. Si llegamos aquí, es que NO ha saltado ninguna excepción. Éxito.
            // Recuperamos el producto ya actualizado para mostrarlo
            Product product = catalog.getProduct(id);
            return product + "\nprod update: ok\n";


        //} catch (AppException e) {
            // Atrapa ValidationException (ID, campos, producto no encontrado)
            //return ErrorHandler.format(e);

        //} catch (Exception e) {
        //    return ErrorHandler.format(e);
        //}
    }
    /*public String execute(String[] args) {
        if (args.length != 5) {
            return "Not valid";
        }

        try {
            int id = Integer.parseInt(args[2]);
            String field = args[3].toUpperCase();
            String value = args[4].replace("\"", "");

            Product product = catalog.getProduct(id);
            if (product == null) {
                return "Product with id " + id + " does not exist";
            }

            boolean ok = catalog.updateProduct(id, field, value);
            if (ok) {
                return product + "\nprod update: ok\n";
            } else {
                switch (field) {
                    case "CATEGORY":
                        return "Invalid category: " + value;
                    case "PRICE":
                        return "Enter a positive value for the price: " + value;
                    default:
                        return "Unknown field: " + field;
                }
            }

            /*if (ok) {
                System.out.println(product);
                System.out.println("prod update: ok\n");
            } else {
                switch (field) {
                    case "CATEGORY":
                        System.out.printf("Invalid category: %s%n", value);
                        break;
                    case "PRICE":
                        System.out.printf("Enter a positive value for the price: %s%n", value);
                        break;
                    default:
                        System.out.printf("Unknown field: %s%n", field);
                }
            }
        } catch (NumberFormatException e) {
            return "Invalid ID format";

        } catch (IllegalArgumentException e) {
            return "Invalid argument";
        }

    }*/
}
