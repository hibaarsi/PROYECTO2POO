package etsisi.poo;

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

           System.out.printf("  %s %s x%d maxUse:%s%n", s.getId(), s.getCategory(), cantidad, s.getSmaxDate());
       }
   }
}
