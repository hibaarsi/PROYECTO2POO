package etsisi.poo.Commands.ProdCommands;

import etsisi.poo.*;
import etsisi.poo.errors.AppException;
import etsisi.poo.errors.ErrorHandler;
import etsisi.poo.errors.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;


public class ProdAddCommand extends AbstractProdAddCommand {
    public ProdAddCommand(Catalog catalog) {
        super(catalog);
    }

    @Override
    public String getPrimerArgumento() {
        return "prod";
    }

    @Override
    public String getSegundoArgumento() {
        return "add";
    }
    public String execute(String[] args) {
        /*if( args.length==4) {
            LocalDate date = LocalDate.parse(args[2]);
            ServiceCategory category = ServiceCategory.valueOf(args[3].toUpperCase());
            Service service = new Service(category, date);
            catalog.addService(service);
            return service.toString();

        }
        return super.execute(args);*/
        if (args.length == 4) {
            try {
                // Validación básica de fecha
                LocalDate date;
                try {
                    date = LocalDate.parse(args[2]);
                } catch (DateTimeParseException e) {
                    throw new ValidationException("Invalid date format: " + args[2]);
                }
                // Validación de categoría
                ServiceCategory category;
                try {
                    category = ServiceCategory.valueOf(args[3].toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Invalid service category: " + args[3]);
                }
                // Crear servicio (puede lanzar validaciones internas)
                Service service = new Service(category, date);
                catalog.addService(service);

                return service.toString();

            } catch (AppException e) {
                return ErrorHandler.format(e);
            } catch (Exception e) {
                return ErrorHandler.format(e);
            }
        }
        // CASO NORMAL: PRODUCTOS (Delega al padre y su try-catch robusto)
        return super.execute(args);

    }
    @Override
    protected Product createProduct(String[] args) {
        if(args.length != 5 && args.length != 6 && args.length != 7){ // Aquí ponía args.length!= 5 && args.length!= 6. No se ejecutaba cuando era personalizable
            throw new ValidationException("Usage: prod add [<id>] \"<name>\" <category> <price> [<maxPersonal>]");
        }
        int id;
        String name;
        Category category;
        double price;

        if(args.length ==5){
            id = 0; // O generar ID automático
            name = parseName(args[2]);
            try {
                category = Category.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid category: " + args[3]);
            }
            price = parsePrice(args[4]);

            return new RegularProduct(id, name, category, price);
        }else{
            // Caso con ID explícito
            id = parseId(args[2]);
            name = parseName(args[3]);
            try {
                category = Category.valueOf(args[4].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid category: " + args[4]);
            }
            price = parsePrice(args[5]);
            if (args.length == 7) { // Personalizable
                int maxPersonal = parseId(args[6]);
                return new ProductPersonalized(id, name, category, price, maxPersonal);
            }
            return new RegularProduct(id, name, category, price);
        }

    }

    @Override
    protected String getOkMessage() {
        return "prod add: ok\n";
    }
}
