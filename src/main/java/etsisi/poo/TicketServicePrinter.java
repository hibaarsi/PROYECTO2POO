package etsisi.poo;

public class TicketServicePrinter implements TicketPrinter {
   @Override
    public void print(TicketModel<?> ticket){
       for (ElementoTicket<?> e : ticket.getElementos()) {
           Service s = (Service) e.getItem();
           // sout
       }
   }
}
