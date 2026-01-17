package etsisi.poo.printers;

import etsisi.poo.products.Category;
import etsisi.poo.products.Product;
import etsisi.poo.products.ProductPersonalized;
import etsisi.poo.products.RegularProduct;
import etsisi.poo.products.services.Service;
import etsisi.poo.tickets.ElementoTicket;
import etsisi.poo.tickets.TicketModel;

import java.util.*;

public class TicketProductoPrinter implements TicketPrinter { // este es igual pero con genericos
    /*@Override
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
    }*/
    @Override
    public void print(TicketModel<?> ticket) {
        if (ticket == null || ticket.getElementos().isEmpty()) {
            System.out.println("Its empty");
            return;
        }

        System.out.println("Ticket : " + ticket.getId());

        // 1. Contar unidades por categoría para ver si aplica descuento
        Map<Category, Integer> unidadesPorCategoria = new HashMap<>();
        List<? extends ElementoTicket<?>> elementos = ticket.getElementos();

        // Ordenamos una copia de la lista para no alterar el ticket original
        List<ElementoTicket<?>> sortedElementos = new ArrayList<>(elementos);
        sortedElementos.sort((e1, e2) -> {
            String n1 = (e1.getItem() instanceof Product) ? ((Product)e1.getItem()).getName() : e1.getItem().toString();
            String n2 = (e2.getItem() instanceof Product) ? ((Product)e2.getItem()).getName() : e2.getItem().toString();
            return n1.compareToIgnoreCase(n2);
        });

        // Contamos categorías
        for (ElementoTicket<?> e : sortedElementos) {
            if (e.getItem() instanceof RegularProduct) {
                RegularProduct p = (RegularProduct) e.getItem();
                unidadesPorCategoria.put(p.getCategory(),
                        unidadesPorCategoria.getOrDefault(p.getCategory(), 0) + e.getQuantity());
            }
        }

        double totalPrice = 0.0;
        double totalDiscount = 0.0;

        // 2. Bucle de Impresión y Cálculos
        for (ElementoTicket<?> e : sortedElementos) {

            // CASO A: Es un PRODUCTO
            if (e.getItem() instanceof Product) {
                Product p = (Product) e.getItem();
                int cantidad = e.getQuantity();
                List<String> personalizations = e.getPersonalizados(); // Importante: Lista de textos

                // --- CÁLCULO DEL PRECIO EFECTIVO ---
                double basePrice = p.getPrice();
                double effectivePrice = basePrice;

                // Si es personalizado y tiene textos, aplicamos regla del 10% por texto
                if (p instanceof ProductPersonalized && personalizations != null && !personalizations.isEmpty()) {
                    double surchargePerText = basePrice * 0.10; // 10% del base
                    effectivePrice += surchargePerText * personalizations.size();
                }

                // --- CÁLCULO DEL DESCUENTO ---
                double perUnitDiscount = 0.0;
                boolean hasDiscount = false;

                if (p instanceof RegularProduct) {
                    Category cat = ((RegularProduct) p).getCategory();
                    // Si hay 2 o más en la categoría, aplicamos % sobre el PRECIO EFECTIVO
                    if (unidadesPorCategoria.getOrDefault(cat, 0) >= 2) {
                        perUnitDiscount = effectivePrice * cat.getDiscount();
                        hasDiscount = true;
                    }
                }

                // --- CONSTRUCCIÓN DEL TEXTO A MOSTRAR ---
                // El toString() por defecto del producto muestra el precio base y no la lista.
                // Tenemos que "fabricar" el string correcto si está personalizado.
                String itemDisplay = p.toString();

                if (p instanceof ProductPersonalized && personalizations != null && !personalizations.isEmpty()) {
                    ProductPersonalized pp = (ProductPersonalized) p;
                    // Reconstruimos el formato exacto del output esperado:
                    // {class:ProductPersonalized, id:5, name:'...', category:..., price:19.5, maxPersonal:3, personalizationList:[red, blue]}
                    itemDisplay = String.format(Locale.US,
                            "{class:ProductPersonalized, id:%s, name:'%s', category:%s, price:%.1f, maxPersonal:%d, personalizationList:%s}",
                            pp.getId(), pp.getName(), pp.getCategory(), effectivePrice, pp.getMaxPersonal(), personalizations.toString());
                } else if (p instanceof ProductPersonalized) {
                    // Caso personalizado pero SIN textos (precio base, sin lista)
                    // Usamos el toString original o reconstruimos para asegurar formato
                    // Si el toString original ya está bien, lo dejamos. Si no, ajusta aquí.
                    ProductPersonalized pp = (ProductPersonalized) p;
                    itemDisplay = String.format(Locale.US,
                            "{class:ProductPersonalized, id:%s, name:'%s', category:%s, price:%.1f, maxPersonal:%d}",
                            pp.getId(), pp.getName(), pp.getCategory(), basePrice, pp.getMaxPersonal());
                }

                // --- IMPRESIÓN ---
                for (int i = 0; i < cantidad; i++) {
                    if (hasDiscount) {
                        // Usamos Locale.US para asegurar puntos en vez de comas
                        System.out.printf(Locale.US, "  %s **discount -%.3f%n", itemDisplay, perUnitDiscount);
                    } else {
                        System.out.println("  " + itemDisplay);
                    }
                }

                // --- ACUMULAR TOTALES ---
                totalPrice += effectivePrice * cantidad;
                totalDiscount += perUnitDiscount * cantidad;
            }

            // CASO B: SERVICIO (Por si acaso se cuela)
            else if (e.getItem() instanceof Service) {
                System.out.println("  " + e.getItem().toString());
            }
        }

        double finalPrice = totalPrice - totalDiscount;
        System.out.printf(Locale.US, "  Total price: %.3f%n", totalPrice); // El ejemplo usa 3 decimales o 1 según caso, ajusta aquí.
        System.out.printf(Locale.US, "  Total discount: %.3f%n", totalDiscount);
        System.out.printf(Locale.US, "  Final Price: %.3f%n", finalPrice);
    }
}
