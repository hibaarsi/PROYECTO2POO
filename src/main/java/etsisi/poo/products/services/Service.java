package etsisi.poo.products.services;

import etsisi.poo.tickets.TicketItem;

import java.time.*;
import java.util.Date;

public class Service implements TicketItem {
    private static int contador = 1;
    private String id;
    private ServiceCategory category;
    private LocalDate smaxDate;

    //mirar lo de la fecha maxima en ticket ( que es donde se cambia la mayoria) y prod add
    public Service(ServiceCategory category, LocalDate smaxDate) {
        this.category = category;
        this.smaxDate = smaxDate;
        this.id = contador + "S";
        contador++;
    }

    public Service(String id, ServiceCategory category, LocalDate smaxDate) {
        this.id = id;
        this.category = category;
        this.smaxDate = smaxDate;
    }

    public static void setNextCounter(int next) {
        contador = next;
    }


    @Override
    public String getId() {
        return id;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    public LocalDate getSmaxDate() {
        return smaxDate;
    }

    public void setSmaxDate(LocalDate smaxDate) {
        this.smaxDate = smaxDate;
    }

    public void setCategory(ServiceCategory category) {
        this.category = category;
    }

    public boolean isExpired() {
        return smaxDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        ZonedDateTime dt= smaxDate.atStartOfDay(ZoneId.systemDefault());
        Date date = Date.from(dt.toInstant());
        return String.format("{class:ProductService, id:%s, category:%s, expiration:%s}",
                id.replace("S", ""), category,date);
                //!isExpired() ? smaxDate : "Expired");
    }
}
