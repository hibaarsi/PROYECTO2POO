package etsisi.poo;

public class TicketEmpresaService extends TicketModel<Service>{
    public TicketEmpresaService(String id){
        super(id);
    }

    @Override
    protected boolean canaddItem(Service item) {
        //return item.isExpired();
        return true;
    }

    @Override
    protected boolean canclose() {
        if(elementos.isEmpty()){
            return false;
        }
        for(ElementoTicket<Service> servicio: elementos){
            Service s= servicio.getItem();
            if(s.isExpired()){
                return false;
            }
        }
        return true;
    }
}
