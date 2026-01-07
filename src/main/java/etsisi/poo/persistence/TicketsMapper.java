package etsisi.poo.persistence;

import etsisi.poo.*;
import etsisi.poo.persistence.dto.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TicketsMapper {

    public TicketsDTO fromModel(TicketController tc, UserController uc) {
        TicketsDTO out = new TicketsDTO();

        for (var entry : tc.getTicketsMap().entrySet()) {
            TicketModel<? extends TicketItem> t = entry.getValue();

            TicketDTO dto = new TicketDTO();
            dto.id = t.getId();
            dto.status = t.getTicketStatus().name();
            dto.openDate = (t.getOpenDate() != null) ? t.getOpenDate().toString() : null;
            dto.endDate = (t.getEndDate() != null) ? t.getEndDate().toString() : null;
            dto.tipo = ticketTypeOf(t);

            dto.cashierId = tc.getCashierIdOfTicket(dto.id);
            dto.clientId = tc.getClientIdOfTicket(dto.id);

            for (ElementoTicket<?> e : t.getElementos()) {
                TicketItemDTO it = new TicketItemDTO();

                TicketItem item = (TicketItem) e.getItem();
                it.itemId = item.getId();
                it.itemType = inferItemType(item);

                it.quantity = e.getQuantity();
                it.personalizados = (e.getPersonalizados() != null)
                        ? new ArrayList<>(e.getPersonalizados())
                        : new ArrayList<>();

                dto.items.add(it);
            }

            out.tickets.put(dto.id, dto);
        }
        return out;
    }

    public void toModel(TicketsDTO dto, TicketController tc, UserController uc, Catalog catalog) {
        if (dto == null || dto.tickets == null) return;

        for (TicketDTO t : dto.tickets.values()) {
            TicketModel<?> model = buildTicketByType(t.id, t.tipo);
            TicketStatus original = TicketStatus.valueOf(t.status);

            // 1) restaurar fechas/status (sin cerrar todavía)
            if (t.openDate != null) model.setOpenDate(LocalDateTime.parse(t.openDate));
            if (t.endDate != null) model.setEndDate(LocalDateTime.parse(t.endDate));
            if (t.status != null) model.setTicketStatus(TicketStatus.valueOf(t.status));

            // pongo status a OPEN temporalmente, añado items y luego se cierra
            boolean shouldBeClosed = "CLOSED".equalsIgnoreCase(t.status);
            if (shouldBeClosed) model.setTicketStatus(TicketStatus.OPEN);
            else model.setTicketStatus(original);

            // 2) asociar a users
            Cashier cashier = uc.getCashier(t.cashierId);
            Client client = uc.getClient(t.clientId);
            if (cashier != null) cashier.addTicket(model);
            if (client != null) client.addTicket(model);

            // 3) meter en mapa global
            tc.putTicket(model.getId(), model);

            // 4) restaurar relación ticket -> cashier/client (para persistencia)
            tc.setTicketOwner(model.getId(), t.cashierId, t.clientId);


            // 5) reconstruir items (método en Catalog para obtener item por id)
            for (TicketItemDTO it : t.items) {
                TicketItem realItem = resolveItem(catalog, it.itemType, it.itemId);
                if (realItem != null) {

                    //REVISAR
                    // Si es evento, restaura número de personas
                    if (realItem instanceof EventProducts ep) {
                        ep.setActualPeople(it.quantity);
                    }

                    // Si es personalized, restaura personalizaciones dentro del producto
                    if (realItem instanceof ProductPersonalized pp && it.personalizados != null) {
                        pp.setPersonalizations(it.personalizados);
                    }

                    ((TicketModel<TicketItem>) model).addItem(
                            realItem,
                            it.quantity,
                            it.personalizados != null ? new ArrayList<>(it.personalizados) : new ArrayList<>()
                    );
                }
            }

            // 6) restaurar CLOSED al final
            if (shouldBeClosed) model.setTicketStatus(TicketStatus.CLOSED);
        }
    }

    private String ticketTypeOf(TicketModel<?> t) {
        if (t instanceof TicketEmpresaService) return "s";
        if (t instanceof TicketEmpresaMix) return "m";
        return "p";
    }

    private TicketModel<?> buildTicketByType(String id, String tipo) {
        if (tipo == null) tipo = "p";
        switch (tipo.toLowerCase()) {
            case "s":
                TicketModel<?> ts = new TicketEmpresaService(id);
                ts.setPrinter(new TicketServicePrinter());
                return ts;
            case "m":
                TicketModel<?> tm = new TicketEmpresaMix(id);
                tm.setPrinter(new TicketMixPrinter());
                return tm;
            default:
                TicketModel<?> tp = new TicketCommon(id);
                tp.setPrinter(new TicketProductoPrinter());
                return tp;
        }
    }

    private String inferItemType(TicketItem item) {
        return (item instanceof Service) ? "SERVICE" : "PRODUCT";
    }

    private TicketItem resolveItem(Catalog catalog, String itemType, String itemId) {
        if (catalog == null || itemId == null) return null;

        if ("SERVICE".equalsIgnoreCase(itemType)) {
            Service s = catalog.getService(itemId);
            if (s == null) return null;
            return new Service(s.getId(), s.getCategory(), s.getSmaxDate()); // COPIA
        }

        try {
            int idInt = Integer.parseInt(itemId);
            Product p = catalog.getProduct(idInt);
            if (p == null) return null;

            if (p instanceof EventProducts ep) {
                return new EventProducts(
                        idInt, ep.getName(), ep.getPrice(), ep.getEventDate(), ep.getMaxPeople(), ep.getEventType()
                );
            }
            if (p instanceof ProductPersonalized pp) {
                return new ProductPersonalized(
                        idInt, pp.getName(), pp.getCategory(), pp.getBasePrice(), pp.getMaxPersonal()
                );
            }
            if (p instanceof RegularProduct rp) {
                return new RegularProduct(idInt, rp.getName(), rp.getCategory(), rp.getPrice());
            }
            return p; // fallback

        } catch (NumberFormatException e) {
            return null;
        }
    }


   /* private TicketItem resolveItem(Catalog catalog, String itemType, String itemId) {
        if (catalog == null || itemId == null) return null;

        if ("SERVICE".equalsIgnoreCase(itemType)) {
            return catalog.getService(itemId);   // id tipo "1S"
        }

        // PRODUCT por defecto (incluye EventProducts, Personalized y Regular)
        try {
            int idInt = Integer.parseInt(itemId);
            return catalog.getProduct(idInt);
        } catch (NumberFormatException e) {
            return null;
        }
    }*/

}
