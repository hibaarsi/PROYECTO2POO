package etsisi.poo;
import java.util.*;

public class TicketController {
    private Map<String, TicketModel<?>> tickets; //este es el mapa global
    private UserController userController;

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
        List<TicketModel> ticketsOfCashier = cashier.getTickets();
        for (TicketModel t : ticketsOfCashier) {
            tickets.remove(t.getId());
            userController.removeTicketFromAnyClient(t);
        }
    }

    public TicketModel<?> newTicket(String ticketID, String cashierID, String userID,String tipo) throws Exception {
        Cashier cashier = userController.getCashier(cashierID);
        if (cashier == null) {
            throw new Exception("No se encontro el ID del cajero.");
        }

        Client client = userController.getClient(userID);
        if (client == null) {
            throw new Exception("No se encontro el ID del cliente.");
        }
        if(tipo==null || tipo.isEmpty()){
            tipo= "p";
        }

        String finalID = ticketID;
        if (finalID == null) {
            finalID = generateUniqueId();
        } else if (tickets.containsKey(finalID)) {
            throw new Exception("El ID del ticket ya existe: " + finalID);
        }

        //TicketModel<?> ticket = new TicketModel(finalID);
        TicketModel<? > ticket;
        switch( tipo){
            case "s":
                if(!(client instanceof ClientEmpresa)){
                    throw new Exception("Solo los clientes de empresa pueden crear tickets de servicio");
                }
                ticket= new TicketEmpresaService(finalID);
                break;

            case "m":
                if(!(client instanceof ClientEmpresa)){
                    throw new Exception("Solo los clientes de empresa pueden crear tickets de servicio");
                }
                ticket = new TicketEmpresaService(finalID);
                 break;
            case "p":
                default:
                    ticket= new TicketCommon(finalID);
                    break;
        }
        tickets.put(ticketID, ticket);
        cashier.addTicket(ticket);
        client.addTicket(ticket);
        return ticket;
    }


    public boolean cashierHasTicket(String cashierId, TicketModel ticket) {
        List<TicketModel> cashierTickets = userController.getCashier(cashierId).getTickets();
        return cashierTickets.contains(ticket);
    }

    public TicketModel<?> getTicket(String id) {
        return tickets.get(id);
    }

    public boolean addItemToTicket(String ticketId, TicketItem item, int cantidad, ArrayList<String> personalizados) {
        TicketModel<?> ticket = getTicket(ticketId);
        if (ticket == null || ticket.isClosed()) {
            return false;
        }

        ticket.addItem(item, cantidad, personalizados);
        return true;
    }

    public boolean removeProductFromTicket(String ticketId, Product product) {
        TicketModel ticket = getTicket(ticketId);
        if (ticket == null || ticket.isClosed()) {
            return false;
        }

        ticket.removeProduct(product);
        return true;

    }

    public List<TicketModel> getTicketsSortedByCashierId() {
        List<TicketModel> sortedTickets = new ArrayList<>();

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
        List<TicketModel> tickets = getTicketsSortedByCashierId();
        for (TicketModel t : tickets) {
            System.out.println("  " + t.getId() + " - " + t.getTicketStatus());
        }
    }

    public void printTicketInfo(TicketModel ticket) {
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
    }
}