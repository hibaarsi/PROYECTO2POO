package etsisi.poo;

public class TicketEmpresaMix extends TicketModel<TicketItem>{ // esta contiene los dos, productos y servicios)
    public TicketEmpresaMix(String id){
        super(id);
    }

    @Override
    protected boolean canaddItem(TicketItem item) {
        return true;
    }

    @Override
    protected boolean canclose() {
        int servicios = 0;
        int productos = 0;
        for (ElementoTicket<TicketItem> e : elementos) {
            if (e.getItem() instanceof Service) {
                servicios++;
            } else if (e.getItem() instanceof Product) {
                productos++;
            }
        }
        return servicios > 0 && productos > 0;
    }
}
