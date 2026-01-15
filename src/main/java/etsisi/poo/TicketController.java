package etsisi.poo;

import etsisi.poo.errors.ValidationException;

import java.util.*;

public class TicketController {

    private Map<String, TicketModel<? extends TicketItem>> tickets; //este es el mapa global
    private UserController userController;

    private Map<String, String> ticketToCashier = new HashMap<>();
    private Map<String, String> ticketToClient = new HashMap<>();


    public TicketController(UserController userController) {
        this.tickets = new HashMap<>();
        this.userController = userController;
    }

    private String generateUniqueId() {
        String newId = "";
        boolean unique = false;

        while (!unique) {
            newId = TicketModel.calculateID();
            if (!tickets.containsKey(newId)) {
                unique = true;
            }
        }
        return newId;
    }

    public void removeTicketsFromCashier(Cashier cashier) {
        if (cashier == null) return;
        List<TicketModel<? extends TicketItem>> ticketsOfCashier = cashier.getTickets();
        for (TicketModel<? extends TicketItem> t : ticketsOfCashier) {
            tickets.remove(t.getId());
            ticketToCashier.remove(t.getId());
            ticketToClient.remove(t.getId());
            userController.removeTicketFromAnyClient(t);
        }
    }

    /*public TicketModel<?> newTicket(String ticketID, String cashierID, String userID, String tipo) throws Exception {
        Cashier cashier = userController.getCashier(cashierID);
        if (cashier == null) {
            throw new Exception("No se encontro el ID del cajero.");
        }

        Client client = userController.getClient(userID);
        if (client == null) {
            throw new Exception("No se encontro el ID del cliente.");
        }
        if (tipo == null || tipo.isEmpty()) {
            tipo = "p";
        }

        String finalID = ticketID;
        if (finalID == null) {
            finalID = generateUniqueId();
        } else if (tickets.containsKey(finalID)) {
            throw new Exception("El ID del ticket ya existe: " + finalID);
        }

        //TicketModel<?> ticket = new TicketModel(finalID);
        TicketModel<?> ticket;
        switch (tipo.toLowerCase()) {
            case "s":
                if (!(client instanceof ClientEmpresa)) {
                    throw new Exception("Solo los clientes de empresa pueden crear tickets de servicio");
                }
                ticket = new TicketEmpresaService(finalID);
                ticket.setPrinter(new TicketServicePrinter());
                break;

            case "m":
                if (!(client instanceof ClientEmpresa)) {
                    throw new Exception("Solo los clientes de empresa mixta pueden crear tickets de servicios y productos ");
                }
                ticket = new TicketEmpresaMix(finalID);
                ticket.setPrinter(new TicketMixPrinter());
                break;
            case "p":
            default:
                ticket = new TicketCommon(finalID);
                ticket.setPrinter(new TicketProductoPrinter());
                break;
        }
        tickets.put(finalID, ticket);
        cashier.addTicket(ticket);
        client.addTicket(ticket);

        ticketToCashier.put(finalID, cashierID);
        ticketToClient.put(finalID, userID);

        return ticket;
    }*/
    // Asegúrate de que este método lance Exception o ValidationException según tu arquitectura
    public TicketModel<?> newTicket(String ticketID, String cashierID, String userID, String tipo) throws Exception {

        // 1. Validaciones previas de existencia
        Cashier cashier = userController.getCashier(cashierID);
        if (cashier == null) {
            throw new ValidationException("Cashier not found: " + cashierID);
        }

        Client client = userController.getClient(userID);
        if (client == null) {
            throw new ValidationException("Client not found: " + userID);
        }

        if (tipo == null || tipo.isEmpty()) {
            tipo = "p";
        }

        // 2. Gestión del ID del Ticket
        String finalID = ticketID;
        if (finalID == null) {
            finalID = generateUniqueId(); // Tu método generador
        } else if (tickets.containsKey(finalID)) {
            throw new ValidationException("Ticket ID already exists: " + finalID);
        }

        TicketModel<?> ticket;

        // 3. SWITCH CORREGIDO
        switch (tipo.toLowerCase()) {
            case "s": // SERVICIO
                if (!(client instanceof ClientEmpresa)) {
                    throw new ValidationException("Solo los clientes de empresa pueden crear tickets de servicio (-s)");
                }
                ticket = new TicketEmpresaService(finalID);
                // Asigna el printer específico que corregimos antes
                ticket.setPrinter(new TicketServicePrinter());
                break;

            case "c": // COMBINADO / MIX (Corregido de "m" a "c")
                if (!(client instanceof ClientEmpresa)) { // Asumiendo que Mix también requiere Empresa
                    throw new ValidationException("Solo los clientes de empresa pueden crear tickets combinados (-c)");
                }
                ticket = new TicketEmpresaMix(finalID);
                // Asigna el printer específico
                ticket.setPrinter(new TicketMixPrinter());
                break;

            case "p": // PRODUCTO (Default)
            default:
                // Ticket común para cualquier cliente
                ticket = new TicketCommon(finalID);
                // Asigna el printer estándar con Locale.US
                ticket.setPrinter(new TicketProductoPrinter());
                break;
        }

        // 4. Registro y vinculaciones
        tickets.put(finalID, ticket);
        cashier.addTicket(ticket);
        client.addTicket(ticket);

        // Mapas de relación inversa (si los usas para búsquedas rápidas)
        ticketToCashier.put(finalID, cashierID);
        ticketToClient.put(finalID, userID);

        return ticket;
    }


    public boolean cashierHasTicket(String cashierId, TicketModel ticket) {
        List<TicketModel<? extends TicketItem>> cashierTickets = userController.getCashier(cashierId).getTickets();
        return cashierTickets.contains(ticket);
    }

    public TicketModel<? extends TicketItem> getTicket(String id) {
        return tickets.get(id);
    }

    public boolean addItemToTicket(String ticketId, TicketItem item, int cantidad, ArrayList<String> personalizados) {
        TicketModel<? extends TicketItem> ticket = getTicket(ticketId);
        if (ticket == null || ticket.isClosed()) {
            return false;
        }
        TicketModel<TicketItem> t = (TicketModel<TicketItem>) ticket;
        t.addItem(item, cantidad, personalizados);
        return true;
    }

    public boolean removeProductFromTicket(String ticketId, TicketItem item) {
        TicketModel<? extends TicketItem> ticket = getTicket(ticketId);
        if (ticket == null || ticket.isClosed()) {
            return false;
        }
        TicketModel<TicketItem> t = (TicketModel<TicketItem>) ticket;
        t.removeItem(item);
        return true;

    }

    public List<TicketModel<? extends TicketItem>> getTicketsSortedByCashierId() {
        List<TicketModel<? extends TicketItem>> sortedTickets = new ArrayList<>();

        List<Cashier> cashiers = userController.getCashiersSortedByID();

        // vamos en orden y sacamos los tickets
        for (Cashier c : cashiers) {
            //añadimos los tickets de este cajero a la lista final y como se hace por orden acaba ordenada
            sortedTickets.addAll(c.getTickets());
        }
        return sortedTickets;
    }

    public void listAllTickets() {
        System.out.println("Ticket List:");
        List<TicketModel<? extends TicketItem>> tickets = getTicketsSortedByCashierId();
        for (TicketModel<? extends TicketItem> t : tickets) {
            System.out.println("  " + t.getId() + " - " + t.getTicketStatus());
        }
    }

    public Map<String, TicketModel<? extends TicketItem>> getTicketsMap() {
        return tickets;
    }

    public void putTicket(String id, TicketModel<? extends TicketItem> ticket) {
        tickets.put(id, ticket);
    }

    public void putTicket(TicketModel<? extends TicketItem> ticket) {
        tickets.put(ticket.getId(), ticket);
    }

    public String getCashierIdOfTicket(String ticketId) {
        return ticketToCashier.get(ticketId);
    }

    public String getClientIdOfTicket(String ticketId) {
        return ticketToClient.get(ticketId);
    }

    public void setTicketOwner(String ticketId, String cashierId, String clientId){
        ticketToCashier.put(ticketId, cashierId);
        ticketToClient.put(ticketId, clientId);
    }



  /*  public void printTicketInfo(TicketModel ticket) {
        if (ticket == null) {
            System.out.println("Ticket ID not found");
            return;
        }
        List<ElementoTicket> elementos = ticket.getElementos();
        if (elementos.isEmpty()) {
            System.out.println("Its empty");
            return;
        }
        //en vez de usar contadores que es ineficiente usamos un hashmap
        Map<Category, Integer> unidadesPorCategoria = new HashMap<>();
        for (ElementoTicket e : elementos) {
            Product p = e.getProduct();
            int cantidad = e.getQuantity();
            if (p instanceof RegularProduct) {
                //pasa de product a regular product porq el get category solo esta en regular product
                Category category = ((RegularProduct) p).getCategory();
                int actual = unidadesPorCategoria.getOrDefault(category, 0);//busca la clave categoria
                //actualiza el mapa sumando la cantidad actual+nuevas uds
                unidadesPorCategoria.put(category, actual + cantidad);
            }
        }
        double totalPrice = 0.0;
        double totalDiscount = 0.0;
        System.out.println("Ticket : " + ticket.getId());
        //ordenar las lineas por nombre
        elementos.sort((e1, e2) ->
                e1.getProduct().getName().compareToIgnoreCase(e2.getProduct().getName()));
        //recorrer de nuevo para imprimir cada linea y calcular totales
        for (ElementoTicket e : elementos) {
            Product p = e.getProduct();
            int cantidad = e.getQuantity();
            double unitPrice = p.getPrice();
            double perUnitDiscount = 0.0;
            boolean tieneDescuento = false;
            if (p instanceof RegularProduct) {
                Category cat = ((RegularProduct) p).getCategory();
                int udsCategoria = unidadesPorCategoria.getOrDefault(cat, 0);

                //si hay 2 o más uds en esa categoría se aplica el descuento
                if (udsCategoria >= 2) {
                    perUnitDiscount = unitPrice * cat.getDiscount();
                    tieneDescuento = true;
                }
            }
            for (int i = 0; i < cantidad; i++) {
                if (tieneDescuento) {
                    System.out.printf("  %s **discount -%.3f%n", p, perUnitDiscount);
                } else {
                    System.out.println("  " + p);
                }
            }
            //actualizamos totales
            double linePrice = unitPrice * cantidad;
            double lineDiscount = perUnitDiscount * cantidad;
            totalPrice += linePrice;
            totalDiscount += lineDiscount;
        }
        double finalPrice = totalPrice - totalDiscount;
        System.out.printf("  Total price: %.3f%n", totalPrice);
        System.out.printf("  Total discount: %.3f%n", totalDiscount);
        System.out.printf("  Final Price: %.3f%n", finalPrice);
    }

    public void swapIdInMapWhenClose(String oldTicketId) {
        TicketModel ticket = tickets.get(oldTicketId);

        if (ticket == null) {
            System.out.println("Error interno: No se encuentra el ticket para cerrar: " + oldTicketId);
            return;
        }

        tickets.remove(oldTicketId);

        //actualiza su estado y calcula su nuevo ID internamente
        ticket.close();

        //lo volvemos a insertar en el mapa con la nueva clave
        tickets.put(ticket.getId(), ticket);
    }

    public void printTicket(String ticketId) {
        TicketModel ticket = getTicket(ticketId);
        if (ticket == null) {
            System.out.println("Ticket ID not found");
            return;
        }

        if (!ticket.isClosed()) {// primero se cierra el ticket si no esta cerrado
            swapIdInMapWhenClose(ticketId);
        }
        printTicketInfo(ticket);
    }*/
}