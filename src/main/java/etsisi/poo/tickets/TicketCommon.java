package etsisi.poo.tickets;

import etsisi.poo.products.Product;

public class TicketCommon extends TicketModel<Product> {
    public TicketCommon(String id){
        super(id);
    }

    @Override
    protected boolean canaddItem(Product item) {
        return true;
    }

    @Override
    protected boolean canclose() {
        return (elementos.size()>0);
    }
}
