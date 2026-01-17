package etsisi.poo;

public class TicketEmpresaMix extends TicketModel<TicketItem>{ // esta contiene los dos, productos y servicios)
    public TicketEmpresaMix(String id){
        super(id);
    }

    @Override
    protected boolean canaddItem(TicketItem item) {
        if(item instanceof Service){
            return !((Service) item).isExpired();
        }
        return true;

    }

    @Override
    protected boolean canclose() {
        int servicios = 0;
        int productos = 0;
        for (ElementoTicket<TicketItem> e : elementos) {
            TicketItem item= e.getItem();
            if (item instanceof Service) {
               servicios++;
               if(((Service) item).isExpired()){
                   return false;
               }
            } else if (item instanceof Product) {
                productos++;
            }
        }
        return servicios > 0 && productos > 0;
    }
}
