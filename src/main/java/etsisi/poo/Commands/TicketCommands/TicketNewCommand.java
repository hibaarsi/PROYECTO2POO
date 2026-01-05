package etsisi.poo.Commands.TicketCommands;

import etsisi.poo.*;
import etsisi.poo.Commands.ICommand;

public class TicketNewCommand implements ICommand {
    private final TicketController ticketController;
    public TicketNewCommand(TicketController ticketController) {
        this.ticketController = ticketController;
    }
    @Override
    public String getPrimerArgumento() {
        return "ticket";
    }

    @Override
    public String getSegundoArgumento() {
        return "new";
    }

    @Override
    public String execute(String[] args) {
        String ticketID = null;
        String cashierID;
        String clientID;
        String tipo = "p";
        if (args.length != 4 && args.length != 5 && args.length != 6) {
            return "Usage: ticket new [<id>] <cashId> < userId> -[c|p|s] (default -p option)";
        }
        try {
            if (args.length == 4) {
                cashierID = args[2];
                clientID = args[3];
            } else if(args.length == 5) {
                ticketID = args[2];
                cashierID = args[3];
                clientID = args[4];
            }else{
                ticketID = args[2];
                cashierID = args[3];
                clientID = args[4];
                tipo= args[5];

            }
            TicketModel<? extends TicketItem> ticket = ticketController.newTicket(ticketID, cashierID, clientID,tipo);
            if(ticket==null){
                return "Ticket not created";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Ticket : ").append(ticket.getId()).append("\n");
            sb.append("  Total price: 0.0").append("\n");
            sb.append("  Total discount: 0.0").append("\n");
            sb.append("  Final Price: 0.0").append("\n");
            sb.append("ticket new: ok\n");

            return sb.toString();

        } catch (Exception e) {
            return e.getMessage();
        }
    }
}

