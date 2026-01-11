package etsisi.poo.Commands.ProdCommands;

import etsisi.poo.Catalog;
import etsisi.poo.Commands.ICommand;
import etsisi.poo.Product;
import etsisi.poo.errors.AppException;
import etsisi.poo.errors.ErrorHandler;
import etsisi.poo.errors.ValidationException;

public abstract class AbstractProdAddCommand implements ICommand {
    protected final Catalog catalog;

    public AbstractProdAddCommand(Catalog catalog) {
        this.catalog = catalog;
    }

    protected int parseId(String idStr) {
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            // Transformamos el error técnico en uno de negocio
            throw new ValidationException("Invalid ID format: " + idStr);
        }
    }

    protected String parseName(String nameStr) {
        if (nameStr == null || nameStr.isEmpty()) {
            throw new ValidationException("Name cannot be empty");
        }
        return nameStr.replace("\"", "");
    }

    protected double parsePrice(String priceStr) {
        try {
            double value = Double.parseDouble(priceStr);
            // Esta validación también la hace el constructor de Product,
            // pero no está de más aquí.
            if (value <= 0) throw new ValidationException("Price must be positive.");
            return value;
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid price format: " + priceStr);
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

        } catch (AppException e) {
            // CAPTURA CENTRALIZADA: Validaciones,
            return ErrorHandler.format(e);

        } catch (Exception e) {
            // Captura de emergencia para bugs imprevistos
            return ErrorHandler.format(e);
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