package etsisi.poo.Commands.ProdCommands;

import etsisi.poo.errors.AppException;
import etsisi.poo.errors.ErrorHandler;
import etsisi.poo.errors.ValidationException;
import etsisi.poo.products.*;
import etsisi.poo.products.services.Service;
import etsisi.poo.products.services.ServiceCategory;

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

                return service.toString() + "\nprod add: ok\n";

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
    /*protected Product createProduct(String[] args) {
        if(args.length != 5 && args.length != 6 && args.length != 7){ // Aquí ponía args.length!= 5 && args.length!= 6. No se ejecutaba cuando era personalizable
            throw new ValidationException("Usage: prod add [<id>] \"<name>\" <category> <price> [<maxPersonal>]");
        }
        int id;
        String name;
        Category category;
        double price;

        if(args.length==5 || args.length==6){
            id = 0; // O generar ID automático
            name = parseName(args[2]);
            try {
                category = Category.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid category: " + args[3]);
            }
            price = parsePrice(args[4]);
            if(args.length==6){
                int maxPersonal = parseId(args[5]);
                return new ProductPersonalized(id, name, category, price, maxPersonal);
            }
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

    }*/
    protected Product createProduct(String[] args) throws ValidationException {

        if (args.length < 5 || args.length > 7) {
            throw new ValidationException("Usage: prod add [<id>] \"<name>\" <category> <price> [<maxPersonal>]");
        }

        int currentIndex = 2; // Empezamos a leer desde el tercer argumento
        int id;
        //int id = 0; // 0 indica autogenerado

        // DETECTAR SI HAY ID EXPLÍCITO
        if (!args[currentIndex].startsWith("\"")) {
            id = parseId(args[currentIndex]);
            currentIndex++; // Avanzamos el índice porque hemos consumido un argumento
        }else {
            // si es el primer elemento sin id se le asigna el 0 y al resto el siguiente libre
            id = catalog.generateNextFreeProductId();
        }
        // Si empezaba por comillas, no entramos al if, id se queda en 0 y currentIndex sigue en 2.

        // VERIFICAR QUE QUEDAN ARGUMENTOS SUFICIENTES
        // Independientemente del ID, necesitamos obligatoriamente: Name, Category, Price (3 argumentos)
        if ((args.length - currentIndex) < 3) {
            throw new ValidationException("Missing mandatory arguments: Name, Category or Price.");
        }

        // LEER DATOS OBLIGATORIOS (Usando currentIndex)
        String name = parseName(args[currentIndex++]);

        Category category;
        try {
            category = Category.valueOf(args[currentIndex++].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid category: " + args[currentIndex-1]);
        }

        double price = parsePrice(args[currentIndex++]);

        // DETECTAR PERSONALIZACIÓN (OPCIONAL)
        // Si todavía quedan argumentos después de leer el precio, significa que hay maxPersonal
        // Esto cumple con la restricción de que productos personalizados tienen maxPers [cite: 17, 69]
        if (currentIndex < args.length) {
            int maxPersonal = Integer.parseInt(args[currentIndex]);
            return new ProductPersonalized(id, name, category, price, maxPersonal);
        }

        return new RegularProduct(id, name, category, price);
    }

    @Override
    protected String getOkMessage() {
        return "prod add: ok\n";
    }
}
