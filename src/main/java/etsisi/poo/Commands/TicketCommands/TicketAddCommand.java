package etsisi.poo.Commands.TicketCommands;

import etsisi.poo.Commands.ICommand;
import etsisi.poo.errors.ValidationException;
import etsisi.poo.products.Catalog;
import etsisi.poo.products.EventProducts;
import etsisi.poo.tickets.ElementoTicket;
import etsisi.poo.tickets.TicketController;
import etsisi.poo.tickets.TicketItem;
import etsisi.poo.tickets.TicketModel;
import etsisi.poo.users.Cashier;
import etsisi.poo.users.UserController;

import java.util.ArrayList;

//REVISAR
public class TicketAddCommand implements ICommand {
    private final TicketController ticketController;
    private final UserController userController;
    private final Catalog catalog;

    public TicketAddCommand(TicketController ticketController, UserController userController, Catalog catalog) {
        this.ticketController = ticketController;
        this.userController = userController;
        this.catalog = catalog;
    }
    @Override
    public String getPrimerArgumento() {
        return "ticket";
    }
    @Override
    public String getSegundoArgumento() {
        return "add";
    }
    /*@Override
    public String execute(String[] args) {
        if (args.length < 6) {
            throw new ValidationException("Usage: ticket add <ticketId> <cashierId> <productId> <quantity> [--p personalization1 --p personalization2 ...]");
        }
        try {
            String ticketId = args[2];
            String cashierId = args[3];
            String productId = args[4];
            int quantity = Integer.parseInt(args[5]);

            ArrayList<String> personalizations = new ArrayList<>();
            for (int i = 6; i < args.length; i++) {
                if (args[i].startsWith("--p")) {
                    String personalization = args[i].substring(3); // despues de --p empieza la personalizacion
                    if (!personalization.isEmpty()) {
                        personalizations.add(personalization);
                    }
                }
            }

           TicketItem product = catalog.getItem(productId);
            if (product == null) {
                return "Product not found";
            }

            TicketModel<? extends TicketItem> ticket = ticketController.getTicket(ticketId);
            if (ticket == null) return "Ticket not found";
            Cashier cashier = userController.getCashier(cashierId);
            if (cashier == null) return "Cashier not found";

            if (product instanceof EventProducts) {
                for (ElementoTicket elementoTicket : ticket.getElementos()) {
                    if ( elementoTicket.getItem().equals(product.getId())) //mirar
                        return "Product already in ticket";

                }
            }
           boolean add= ticketController.addItemToTicket(ticketId, product, quantity, personalizations);
            if(!add) return "Product not added";
            ticket.print();
        } catch (NumberFormatException e) {
            return "Invalid number format for product ID or quantity";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return "ticket add: ok\n";
    }*/
    @Override
    public String execute(String[] args) throws ValidationException {
        // 1. Validación mínima de argumentos (ticket, add, ticketId, cashierId, prodId) -> Mínimo 5
        if (args.length < 5) {
            throw new ValidationException("Usage: ticket add <ticketId> <cashierId> <productId> [quantity] [--p...]");
        }

        String ticketId = args[2];
        String cashierId = args[3];
        String rawProdId = args[4];

        // Recuperamos el ticket y el cajero una sola vez para validar existencia
        // (Esto es común para ambos casos)
        TicketModel<? extends TicketItem> ticket = ticketController.getTicket(ticketId);
        if (ticket == null) {
            throw new ValidationException("Ticket not found: " + ticketId);
        }

        // Opcional: Validar cajero aquí o dejar que el controller lo haga.
        // Siguiendo tu snippet, validamos aquí:
        Cashier cashier = userController.getCashier(cashierId);
        if (cashier == null) {
            throw new ValidationException("Cashier not found: " + cashierId);
        }

        // ---------------------------------------------------------
        // CASO A: SERVICIOS (El ID termina en 'S', ej: "1S")
        // ---------------------------------------------------------
        if (rawProdId.toUpperCase().endsWith("S")) {
            // Regla: Los servicios se añaden SIN cantidad (Length debe ser 5)
            if (args.length != 5) {
                throw new ValidationException("Usage for Services: ticket add <ticketId> <cashierId> <serviceId>");
            }

            // 1. BUSCAR EL OBJETO SERVICIO
            // Necesitas obtener el objeto TicketItem correspondiente al servicio.
            // Asumo que tu catálogo tiene un método para buscar servicios por String ID.
            // Si no tienes 'getService', usa el método que tengas para recuperar servicios.
            TicketItem serviceItem = catalog.getService(rawProdId);

            if (serviceItem == null) {
                throw new ValidationException("Service not found: " + rawProdId);
            }

            // 2. LLAMAR AL MÉTODO GENÉRICO
            // Truco: Pasamos cantidad 1 y lista vacía para cumplir con la firma del método.
            boolean added = ticketController.addItemToTicket(ticketId, serviceItem, 1, new ArrayList<>());

            if (!added) {
                // Este error saltará si el ticket está cerrado o si 'canAddItem' devuelve false
                throw new ValidationException("Service could not be added (Check ticket status or compatibility).");
            }

        }
        // ---------------------------------------------------------
        // CASO B: PRODUCTOS ESTÁNDAR (ID Numérico)
        // ---------------------------------------------------------
        else {
            // Regla: Necesitan cantidad (Length mínima 6)
            if (args.length < 6) {
                throw new ValidationException("Usage for Products: ticket add <ticketId> <cashierId> <prodId> <quantity> [--p...]");
            }

            int productId;
            int quantity;

            try {
                productId = Integer.parseInt(rawProdId);
                quantity = Integer.parseInt(args[5]);
            } catch (NumberFormatException e) {
                throw new ValidationException("Product ID and Quantity must be integers.");
            }

            // Procesar personalizaciones
            ArrayList<String> personalizations = new ArrayList<>();
            if (args.length > 6) {
                for (int i = 6; i < args.length; i++) {
                    if (args[i].startsWith("--p")) {
                        String pText = args[i].substring(3);
                        if (!pText.isEmpty()) {
                            personalizations.add(pText);
                        }
                    }
                }
            }

            // Validaciones de negocio del Producto
            TicketItem product = catalog.getProduct(productId);
            if (product == null) {
                throw new ValidationException("Product not found: " + productId);
            }

            // Restricción: No duplicar Eventos (Comidas/Reuniones)
            if (product instanceof EventProducts) {
                for (ElementoTicket elementoTicket : ticket.getElementos()) {
                    // Comparamos IDs para ver si ya está en el ticket
                    if (elementoTicket.getItem().getId() == product.getId()) {
                        throw new ValidationException("Product already in ticket (EventProducts cannot be duplicated).");
                    }
                }
            }

            // Llamada al controlador para añadir producto
            boolean added = ticketController.addItemToTicket(ticketId, product, quantity, personalizations);
            if (!added) {
                throw new ValidationException("Product could not be added to ticket.");
            }
        }

        // ---------------------------------------------------------
        // FINALIZACIÓN COMÚN
        // ---------------------------------------------------------

        // Imprime el estado del ticket por consola (Side effect pedido)
        ticket.print();

        // Retorna el mensaje de éxito
        return "ticket add: ok\n";
    }

}