package etsisi.poo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketProductoPrinter implements TicketPrinter { // este es igual pero con genericos
    @Override
    public void print(TicketModel<?> ticket){
        if (ticket == null) {
            System.out.println("Ticket ID not found");
            return;
        }
        List<? extends ElementoTicket<?>> elementos = ticket.getElementos();
        if (elementos.isEmpty()) {
            System.out.println("Its empty");
            return;
        }
        //en vez de usar contadores que es ineficiente usamos un hashmap
        Map<Category, Integer> unidadesPorCategoria = new HashMap<>();
        for (ElementoTicket<?> e : elementos) {
            Product p = (Product) e.getItem();
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
                ((Product) e1.getItem()).getName()
                        .compareToIgnoreCase(((Product) e2.getItem()).getName()));
        //recorrer de nuevo para imprimir cada linea y calcular totales
        for (ElementoTicket e : elementos) {
            Product p =(Product) e.getItem();
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
}
