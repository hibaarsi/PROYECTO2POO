package etsisi.poo.Commands.TicketCommands;

import etsisi.poo.Commands.ICommand;
import etsisi.poo.errors.ValidationException;
import etsisi.poo.printers.TicketPrinter;
import etsisi.poo.tickets.TicketController;
import etsisi.poo.tickets.TicketModel;

public class TicketPrintCommand implements ICommand {
    //no imprime directamente, elige quien imprime el ticket
    private final TicketController ticketController;

    public TicketPrintCommand(TicketController ticketController) {
        this.ticketController = ticketController;
    }

    @Override
    public String getPrimerArgumento() {
        return "ticket";
    }

    @Override
    public String getSegundoArgumento() {
        return "print";
    }

    @Override
    public String execute(String[] args) {
        if (args == null || args.length < 4) {
            throw new ValidationException("Usage: ticket print <ticketId> <cashierId>");
        }

        String ticketId = args[2];
        String cashierId = args[3];

        //TicketModel<?> ticket = ticketController.getTicket(ticketId); //se obtiene el ticket desde el controlador
        TicketModel<?> ticket = ticketController.getTicketByBaseOrFullId(ticketId);

        if (ticket == null) return "Ticket ID not found";

        if (!ticketController.cashierHasTicket(cashierId, ticket)) {//comprobar q el cajero tiene este ticket
            return "This ticket does not belong to cashier " + cashierId;
        }

       /* TicketPrinter printer;
            if(ticket instanceof TicketEmpresaMix){
                printer = new TicketMixPrinter();
            }else if(ticket instanceof  TicketEmpresaService){ // si el ticket es de servivivos
                printer = new TicketServicePrinter();
            }else {
                printer = new TicketProductoPrinter();
            } // solo se decide quien va a imprimir

            */


        TicketPrinter printer = ticket.getPrinter();
        if (printer == null) {
            return "Printer not found";
        }
        ticket.close();
        printer.print(ticket);
        //ticket.close();//imprime el que he elegido

        return "ticket print: ok\n";

        //antes se decidia y se pintaba junto, ahora el sistema decide y otro objeto pinta
    }

}
