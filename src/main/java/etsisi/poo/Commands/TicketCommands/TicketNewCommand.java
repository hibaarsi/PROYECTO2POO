package etsisi.poo.Commands.TicketCommands;

import etsisi.poo.Commands.ICommand;
import etsisi.poo.errors.ValidationException;
import etsisi.poo.tickets.TicketController;
import etsisi.poo.tickets.TicketItem;
import etsisi.poo.tickets.TicketModel;

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

    /*@Override
    public String execute(String[] args) {
        String ticketID = null;
        String cashierID;
        String clientID;
        String tipo = "p";
        if (args.length != 4 && args.length != 5 && args.length != 6) {
            throw new ValidationException("Usage: ticket new [<id>] <cashId> < userId> -[c|p|s] (default -p option)");
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
    }*/
    @Override
    public String execute(String[] args) throws ValidationException {
        // 1. Validar rango de argumentos
        // Mínimo 4: ticket new <cashId> <userId>
        // Máximo 6: ticket new <id> <cashId> <userId> -[c|p|s]
        if (args.length < 4 || args.length > 6) {
            throw new ValidationException("Usage: ticket new [<id>] <cashId> <userId> -[c|p|s] (default -p)");
        }

        String ticketID = null;
        String cashierID;
        String clientID;
        String tipo = "p"; // Valor por defecto si no se especifica "-x"

        // 2. DETECTAR EL TIPO (Flag al final)
        int endArgsIndex = args.length;
        String lastArg = args[args.length - 1];

        // Si el último argumento empieza por "-", asumimos que es el tipo
        if (lastArg.startsWith("-")) {
            // Quitamos el guion ("-s" -> "s") y lo pasamos a minúsculas
            tipo = lastArg.substring(1).toLowerCase();
            endArgsIndex--; // Reducimos el índice para indicar que el último arg ya ha sido procesado
        }

        // 3. ASIGNAR VARIABLES SEGÚN LOS DATOS RESTANTES
        // args[0] es "ticket", args[1] es "new". Los datos útiles empiezan en args[2] hasta endArgsIndex.
        int dataCount = endArgsIndex - 2;

        if (dataCount == 2) {
            // Caso: ticket new <cash> <user> [tipo]
            // No hay ticketID explícito (será null y se autogenerará en el controller)
            cashierID = args[2];
            clientID = args[3];
        } else if (dataCount == 3) {
            // Caso: ticket new <id> <cash> <user> [tipo]
            ticketID = args[2];
            cashierID = args[3];
            clientID = args[4];
        } else {
            // Estructura extraña que no encaja con la lógica
            throw new ValidationException("Invalid arguments structure. Check command syntax.");
        }

        // 4. LLAMADA AL CONTROLADOR Y FORMATO DE SALIDA
        try {
            TicketModel<? extends TicketItem> ticket = ticketController.newTicket(ticketID, cashierID, clientID, tipo);

            if (ticket == null) {
                return "Ticket not created";
            }

            // Construcción idéntica al Output de referencia
            StringBuilder sb = new StringBuilder();
            sb.append("Ticket : ").append(ticket.getId()).append("\n");
            sb.append("  Total price: 0.0").append("\n");
            sb.append("  Total discount: 0.0").append("\n");
            sb.append("  Final Price: 0.0").append("\n");
            sb.append("ticket new: ok\n");

            return sb.toString();

        } catch (Exception e) {
            // Capturamos cualquier error lógico (cliente no válido, ID duplicado) y lo mostramos bonito
            throw new ValidationException(e.getMessage());
        }
    }
}

