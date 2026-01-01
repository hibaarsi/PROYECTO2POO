package etsisi.poo;

public class TicketEmpresaService extends TicketModel<Service>{
    public TicketEmpresaService(String id){
        super(id);
    }

    @Override
    protected boolean canaddItem(Service item) {
        return true;
    }

    @Override
    protected boolean canclose() {
        return (elementos.size()>0);
    }
}
