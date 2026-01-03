package etsisi.poo.Commands.ProdCommands;

import etsisi.poo.Catalog;
import etsisi.poo.Commands.ICommand;
import etsisi.poo.Product;

public abstract class AbstractProdAddCommand implements ICommand {
    protected final Catalog catalog;

    public AbstractProdAddCommand(Catalog catalog) {
        this.catalog = catalog;
    }

    protected int parseId(String idStr) { //protected para que solo se use en este paquete
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID: must be an integer.");
            return -1;
        }
    }

    protected String parseName(String nameStr) {
        return nameStr.replace("\"", "");
    }

    protected double parsePrice(String priceStr) {
        try {
            double value = Double.parseDouble(priceStr);
            if (value <= 0) throw new IllegalArgumentException("Price must be positive.");
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Invalid price: must be a number.");
            return -1;
        }
    }

    protected abstract Product createProduct(String[] args);

    protected abstract String getOkMessage();

    @Override
    public String execute(String[] args) {
        try {
            Product product = createProduct(args);
            boolean ok = catalog.addProduct(product);

            if (ok) {
                return product + "\n" + getOkMessage();
            } else {
                return "Error processing ->" + getPrimerArgumento() + " " +
                        getSegundoArgumento() + " ->Error adding product";
            }

        } catch (Exception e) {
            return "Error processing ->" + getPrimerArgumento() + " " +
                    getSegundoArgumento() + " ->" + e.getMessage();
        }

           /* if (ok) {
                System.out.println(product);
                System.out.println(getOkMessage());
            } else {
                System.out.println("Error processing ->" + getPrimerArgumento() + " " + getSegundoArgumento()
                        + " ->Error adding product");
            }

        } catch (Exception e) {
            System.out.println("Error processing ->" + getPrimerArgumento() + " " + getSegundoArgumento() +
                    " ->" + e.getMessage());
        }
        return null; */

    }
}