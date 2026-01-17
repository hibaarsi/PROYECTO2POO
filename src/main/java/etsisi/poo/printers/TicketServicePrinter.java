package etsisi.poo.printers;

import etsisi.poo.tickets.ElementoTicket;
import etsisi.poo.products.services.Service;
import etsisi.poo.tickets.TicketModel;

public class TicketServicePrinter implements TicketPrinter {
   //estrategia de impresion
    @Override
    public void print(TicketModel<?> ticket){

       if(ticket==null){
           System.out.println("Ticket ID not found");
           return;
       }

       if(ticket.getElementos().isEmpty()){
           System.out.println("Its empty");
           return;
       }

       System.out.println("Ticket : "+ticket.getId());
        System.out.println("Type   : COMPANY SERVICES");
        System.out.println("Services Included:");

       for (ElementoTicket<?> e : ticket.getElementos()) {
           int cantidad =e.getQuantity();
           Service s = (Service) e.getItem();
           s.toString();
          // System.out.printf("  %s %s  expiration:%s%n", s.getId(), s.getCategory(), s.getSmaxDate());
       }
   }
}
