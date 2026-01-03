package etsisi.poo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TicketMixPrinter implements TicketPrinter{
    //  En estos casos, cuando se realice la
    //impresión, los servicios no aparecerán con precio y se aplicara un plus de 15% de descuento en
    //los productos por cada servicio contratado
    @Override
    public void print(TicketModel<?> ticket){
        if (ticket==null){
            System.out.println("Ticket null");
            return;
        }
        List<ElementoTicket<?>> elementos = ticket.getElementos();
        if (elementos == null || elementos.isEmpty()) {
            System.out.println("Its empty");
            return;
        }
        //para contar los servicios
        int numServicios=0;
        for (ElementoTicket<?>e:elementos){
            if (e.getItem()instanceof Service){
                numServicios+=e.getQuantity();
            }
        }
        double extraRate=Math.min(1.0,0.15*numServicios);
        System.out.println("Ticket : " + ticket.getId());
        System.out.println("Type   : COMPANY COMBINED");
        System.out.println("Extra discount (services): " + Math.round(extraRate * 100) + "%");
        System.out.println();

        //se separan los prod y los servicios en 2 array
        List<ElementoTicket<?>> lineasProductos = new ArrayList<>();
        List<ElementoTicket<?>> lineasServicios = new ArrayList<>();
        for (ElementoTicket<?> e : elementos) {
            if (e.getItem() instanceof Product) lineasProductos.add(e);
            else if (e.getItem() instanceof Service) lineasServicios.add(e);
        }

        System.out.println("PRODUCTS");
        lineasProductos.sort(Comparator.comparing(
                e -> ((Product) e.getItem()).getName(),
                String.CASE_INSENSITIVE_ORDER
        ));

        double totalPrice = 0.0;
        double totalDiscount = 0.0;

        for (ElementoTicket<?> e : lineasProductos) {
            Product p = (Product) e.getItem();
            int cantidad = e.getQuantity();

            double unitPrice = p.getPrice();

            double unitDiscount = unitPrice * extraRate;

            double linePrice = unitPrice * cantidad;
            double lineDiscount = unitDiscount * cantidad;

            totalPrice += linePrice;
            totalDiscount += lineDiscount;

            System.out.printf("  %s x%d  unit: %.3f  disc: %.3f  line: %.3f%n",
                    p.getName(), cantidad, unitPrice, unitDiscount, (linePrice - lineDiscount));


        }
        double finalPrice = totalPrice - totalDiscount;
        //servicios
        System.out.println();
        System.out.println("SERVICES (no price)");
        for (ElementoTicket<?> e : lineasServicios) {
            Service s = (Service) e.getItem();


            System.out.printf("  %s  %s  maxUse:%s%n",
                    s.getId(),
                    s.getCategory(),
                    s.getSmaxDate());
        }
        System.out.println();
        System.out.printf("  Total price: %.3f%n", totalPrice);
        System.out.printf("  Total discount: %.3f%n", totalDiscount);
        System.out.printf("  Final Price: %.3f%n", finalPrice);
    }
}
