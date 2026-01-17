package etsisi.poo.printers;


import etsisi.poo.tickets.ElementoTicket;
import etsisi.poo.products.Product;
import etsisi.poo.products.services.Service;
import etsisi.poo.tickets.TicketModel;

import java.util.*;

public class TicketMixPrinter implements TicketPrinter {

    @Override
    public void print(TicketModel<?> ticket) {
        if (ticket == null) {
            System.out.println("Ticket null");
            return;
        }

        List<? extends ElementoTicket<?>> elementos = ticket.getElementos();
        if (elementos == null || elementos.isEmpty()) {
            System.out.println("Its empty");
            return;
        }

        // 1) Separar productos y servicios + contar número de servicios (con cantidad)
        List<Object> services = new ArrayList<>();
        List<ElementoTicket<?>> products = new ArrayList<>();
        int numServicios = 0;

        for (ElementoTicket<?> e : elementos) {
            Object it = e.getItem();

            // para que funcione con la clase service
            if (it instanceof Service) {
                services.add(it);
                numServicios += e.getQuantity();
            } else if (it.getClass().getSimpleName().equals("ProductService")) {
                services.add(it);
                numServicios += e.getQuantity();
            } else {
                products.add((ElementoTicket<?>) e);
            }
        }


        // 2) Extra descuento por servicios: 15% por servicio (capado a 100%)
        double extraRate = Math.min(1.0, 0.15 * numServicios);

        System.out.println("Ticket : " + ticket.getId());

        // SERVICES
        if (!services.isEmpty()) {
            System.out.println("Services Included: ");
            for (Object s : services) {
                System.out.println("  " + s);
            }
        }

        // PRODUCT INCLUDED
        if (!products.isEmpty()) {
            System.out.println("Product Included");

            double totalPrice = 0.0;

            // En el output bueno se imprime “1 línea” del Meeting ya con precio total y actual people.
            // Por eso el FIX REAL debe estar en addItem (EventProducts). Aquí solo sumamos.
            for (ElementoTicket<?> e : products) {
                Object it = e.getItem();
                int qty = e.getQuantity();

                if (it instanceof Product p) {
                    // Si es producto normal, puede haber qty>1, pero en el ejemplo es Meeting ya normalizado a qty=1
                    for (int i = 0; i < qty; i++) {
                        System.out.println("  " + p);
                    }
                    totalPrice += p.getPrice() * qty;
                } else {
                    // si no es Product, lo imprimimos tal cual
                    for (int i = 0; i < qty; i++) {
                        System.out.println("  " + it);
                    }
                }
            }

            double extraDiscount = totalPrice * extraRate;
            double totalDiscount = extraDiscount;
            double finalPrice = totalPrice - totalDiscount;

            System.out.printf(Locale.US, "  Total price: %.1f%n", totalPrice);
            System.out.printf(Locale.US,
                    "  Extra Discount from services:%.1f **discount -%.1f%n",
                    extraDiscount, extraDiscount);
            System.out.printf(Locale.US, "  Total discount: %.1f%n", totalDiscount);
            System.out.printf(Locale.US, "  Final Price: %.1f%n", finalPrice);
        }
    }
}