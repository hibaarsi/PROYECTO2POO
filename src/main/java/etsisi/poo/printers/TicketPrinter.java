package etsisi.poo.printers;

import etsisi.poo.tickets.TicketModel;

public interface TicketPrinter {
   void print(TicketModel<? > ticket);
}
