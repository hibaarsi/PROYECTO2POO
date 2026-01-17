package etsisi.poo.tickets;

import etsisi.poo.products.services.Service;
import etsisi.poo.errors.ValidationException;

public class TicketEmpresaService extends TicketModel<Service> {
    public TicketEmpresaService(String id){
        super(id);
    }

    @Override
    protected boolean canaddItem(Service item) {
       if(!item.isExpired()) {
           return true;
       }else{
           throw new ValidationException("El servicio " + item.getId() + " ha expirado");
       }


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
