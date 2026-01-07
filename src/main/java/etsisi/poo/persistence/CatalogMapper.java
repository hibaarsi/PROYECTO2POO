package etsisi.poo.persistence;

import etsisi.poo.*;
import etsisi.poo.persistence.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CatalogMapper {

    public CatalogDTO fromModel(Catalog catalog) {
        CatalogDTO dto = new CatalogDTO();

        // products
        for (Product p : catalog.getProducts().values()) {
            ProductDTO pd = new ProductDTO();
            pd.id = Integer.parseInt(p.getId());
            pd.name = p.getName();
            pd.price = p.getPrice();

            if (p instanceof EventProducts ep) {
                pd.type = "EVENT";
                pd.eventDate = ep.getEventDate().toString();
                pd.maxPeople = ep.getMaxPeople();
                pd.eventType = ep.getEventType().name();
            } else if (p instanceof ProductPersonalized pp) {
                pd.type = "PERSONALIZED";
                pd.category = pp.getCategory().name();
                pd.maxPersonal = pp.getMaxPersonal();
                pd.price = pp.getBasePrice();
            } else {
                pd.type = "REGULAR";
                if (p.getCategory() != null) pd.category = p.getCategory().name();
            }

            dto.products.add(pd);
        }

        // services
        for (Service s : catalog.getServices().values()) {
            ServiceDTO sd = new ServiceDTO();
            sd.id = s.getId();
            sd.category = s.getCategory().name();
            sd.smaxDate = s.getSmaxDate().toString();
            dto.services.add(sd);
        }

        return dto;
    }

    public void toModel(CatalogDTO dto, Catalog catalog) {
        if (dto == null) return;

        // services first (no dependency, but helps)
        int maxServiceNumber = 0;

        if (dto.services != null) {
            for (ServiceDTO s : dto.services) {
                ServiceCategory cat = ServiceCategory.valueOf(s.category);
                LocalDate date = LocalDate.parse(s.smaxDate);

                Service service = new Service(s.id, cat, date);
                catalog.addService(service);

                // calcular max contador
                int n = parseServiceNumber(s.id);
                if (n > maxServiceNumber) maxServiceNumber = n;
            }
        }

        // restaurar contador
        Service.setNextCounter(maxServiceNumber + 1);

        // products
        if (dto.products != null) {
            for (ProductDTO p : dto.products) {
                Product prod = buildProduct(p);
                if (prod != null) catalog.addProduct(prod);
            }
        }
    }

    private int parseServiceNumber(String id) {
        // "12S" -> 12
        if (id == null) return 0;
        try {
            return Integer.parseInt(id.replace("S", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Product buildProduct(ProductDTO p) {
        if (p == null) return null;

        switch (p.type) {
            case "EVENT":
                return new EventProducts(
                        p.id,
                        p.name,
                        p.price,
                        LocalDateTime.parse(p.eventDate),
                        p.maxPeople,
                        EventType.valueOf(p.eventType)
                );

            case "PERSONALIZED":
                return new ProductPersonalized(
                        p.id,
                        p.name,
                        Category.valueOf(p.category),
                        p.price,
                        p.maxPersonal
                );

            default: // REGULAR
                return new RegularProduct(
                        p.id,
                        p.name,
                        Category.valueOf(p.category),
                        p.price
                );
        }
    }
}
