package etsisi.poo;

import java.time.LocalDate;

public class Service implements TicketItem{
    private static int contador=1;
    private String id;
    private ServiceCategory category;
    private LocalDate smaxDate;

    //mirar lo de la fecha maxima en ticket ( que es donde se cambia la mayoria) y prod add
    public Service(ServiceCategory category, LocalDate smaxDate) {
        this.category = category;
        this.smaxDate = smaxDate;
        this.id= contador+ "S";
        contador++;
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

    @Override
    public String toString() {
        return String.format("{class:Service, id:%s, category:%s, smaxDate:%s}",
                id, category, smaxDate);
    }
}
