package etsisi.poo;

import etsisi.poo.errors.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class TicketModel<T extends TicketItem> {
    protected String id;
    protected TicketStatus ticketStatus;
    //protected ArrayList<Product> products;
    //protected List<Service> services;
    protected LocalDateTime openDate;
    protected LocalDateTime endDate;
    protected TicketPrinter printer;
    protected List<ElementoTicket<T>> elementos;
    //protected T ticketType;
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm");

    public TicketModel(String id) {
        this.id = id;
        this.elementos = new ArrayList<>();
        // this.products = new ArrayList<>();
        this.ticketStatus = TicketStatus.EMPTY;
        this.openDate = LocalDateTime.now();
    }

    protected abstract boolean canaddItem(T item);

    protected abstract boolean canclose();

    public List<ElementoTicket<T>> getElementos() {//para leer desde fuera las lineas del ticket
        return elementos;
    }

    public void setPrinter(TicketPrinter printer) {
        this.printer = printer;
    }

    public TicketPrinter getPrinter() {
        return printer;
    }

    public void print() {
        printer.print(this);
    }

    public void addItem(T item, int quantity, ArrayList<String> personalizados) {
        if (isClosed()) {
            throw new ValidationException("No se pueden añadir items: el ticket está cerrado.");
        }

        if (!canaddItem(item)) {
            throw new ValidationException("No se puede añadir ese tipo de item a este ticket.");
        }

        elementos.add(new ElementoTicket<>(item, quantity, personalizados));
        if (ticketStatus == TicketStatus.EMPTY) {
            ticketStatus = TicketStatus.OPEN;
        }
    }

    public void removeItem(T item) {
        if (isClosed()) {
            throw new ValidationException("You cant add more products, its closed");
        }

        Iterator<ElementoTicket<T>> elementoTicket = elementos.iterator();
        while (elementoTicket.hasNext()) {
            ElementoTicket e = elementoTicket.next();
            if (e.getItem().getId().equals(item.getId())) {  // antes: (e.getItem().getId() == item.getId())
                elementoTicket.remove();
            }
        }

        if (elementos.isEmpty()) {
            ticketStatus = TicketStatus.EMPTY;
        }
    }

    public static String calculateID() {
        String baseId = LocalDateTime.now().format(DATE_FORMATTER);
        Random random = new Random();
        return baseId + "-" + String.format("%05d", random.nextInt(100000));
    }

  /*  public void addProduct(Product product, int cantidad, ArrayList<String> personalizados) {
        if (isClosed()) {
            System.out.println("You cant add more products, its closed");
            return;
        }

        if (product instanceof ProductPersonalized pp && personalizados != null && !personalizados.isEmpty()) {
            ProductPersonalized productWithPersonalization = new ProductPersonalized(
                    pp.getId(),
                    pp.getName(),
                    pp.getCategory(),
                    pp.getBasePrice(), //precio base sin personalizaciones
                    pp.getMaxPersonal()
            );
            productWithPersonalization.setPersonalizations(personalizados);
            elementos.add(new ElementoTicket(productWithPersonalization, cantidad, personalizados));
            products.add(productWithPersonalization);

        } else if (product instanceof EventProducts) {
            for (ElementoTicket e : elementos) {
                if (e.getItem() instanceof EventProducts) {
                    System.out.println("No se pueden añadir productos de tipo comida o reunion");
                    return;
                }
            }

            ((EventProducts) product).setActualPeople(cantidad);

            double finalPrice = product.getPrice() * cantidad;
            product.setPrice(finalPrice);

            elementos.add(new ElementoTicket(product, 1, personalizados)); //se añade 1 vez solo
            products.add(product);

        } else {
            ElementoTicket elemento = new ElementoTicket(product, cantidad, personalizados);
            elementos.add(elemento);
            products.add(product);
        }

        if (ticketStatus == TicketStatus.EMPTY) {
            ticketStatus = TicketStatus.OPEN;
        }
    }

    public void removeProduct(Product product) {
        if (isClosed()) {
            System.out.println("You cant add more products, its closed");
            return;
        }

        // con iteradores primero lo eliminados de la lista de elementos todas las instancias y de los productos
        Iterator<ElementoTicket<T>> elementoTicket = elementos.iterator();
        while (elementoTicket.hasNext()) {
            ElementoTicket e = elementoTicket.next();
            if (e.getItem().getId() == product.getId()) {
                elementoTicket.remove();
            }
        }

        Iterator<Product> producto = products.iterator();
        while (producto.hasNext()) {
            Product p = producto.next();
            if (p.getId() == product.getId()) {
                producto.remove();
            }
        }

        products.remove(product);
        if (products.isEmpty())
            ticketStatus = TicketStatus.EMPTY;

    }*/

    public void close() {
        if (!canclose()) {
            throw new ValidationException("It cant close");
        }

        if (!isClosed()) {
            ticketStatus = TicketStatus.CLOSED;
            endDate = LocalDateTime.now();
            String cierre = "-" + endDate.format(DATE_FORMATTER);
            id += cierre;
        }
    }

    public boolean isClosed() {
        return ticketStatus == TicketStatus.CLOSED;
    }

    public String getId() {
        return id;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setOpenDate(LocalDateTime openDate) {
        this.openDate = openDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setTicketStatus(TicketStatus status) {
        this.ticketStatus = status;
    }

    public LocalDateTime getOpenDate() {
        return openDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
}