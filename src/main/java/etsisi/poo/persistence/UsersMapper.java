package etsisi.poo.persistence;

import etsisi.poo.errors.PersistenceException;
import etsisi.poo.persistence.dto.*;
import etsisi.poo.users.Cashier;
import etsisi.poo.users.Client;
import etsisi.poo.users.ClientEmpresa;
import etsisi.poo.users.UserController;

public class UsersMapper {

    // MODEL -> DTO
    public UsersDTO fromModel(UserController uc) {
        UsersDTO dto = new UsersDTO();

        // Cashiers
        for (Cashier c : uc.listCashiersIt()) {
            CashierDTO cdto = new CashierDTO();
            cdto.id = c.getID();
            cdto.name = c.getName();
            cdto.email = c.getEmail();
            dto.cashiers.add(cdto);
        }

        // Clients
        for (Client c : uc.listClientsIt()) {
            ClientDTO cldto = new ClientDTO();
            cldto.id = c.getID();
            cldto.name = c.getName();
            cldto.email = c.getEmail();
            cldto.type = (c instanceof ClientEmpresa) ? "EMPRESA" : "NORMAL";
            cldto.cashierId = (c.getCashier() != null) ? c.getCashier().getID() : null;
            dto.clients.add(cldto);
        }

        return dto;
    }

    // DTO -> MODEL
    public void toModel(UsersDTO dto, UserController uc) {
        if (dto == null) return;

        // 1) Crear primero los cashiers (porque los clients dependen de ellos)
        if (dto.cashiers != null) {
            for (CashierDTO c : dto.cashiers) {
                Cashier cashier = new Cashier(c.name, c.email, c.id);
                uc.addCashier(cashier);
            }
        }

        // 2) Crear clients y asociarlos a su cashier
        if (dto.clients != null) {
            for (ClientDTO c : dto.clients) {
                Cashier cashier = (c.cashierId != null) ? uc.getCashier(c.cashierId) : null;

                // Si falta cashier, lo creamos sin asociar?
                // Como vuestro Client constructor requiere cashier, aquí lo más estable es:
                if (cashier == null) {
                    throw new PersistenceException(
                            "Cliente " + c.id + " referencia un cajero inexistente: " + c.cashierId
                    );
                }


                Client client = "EMPRESA".equalsIgnoreCase(c.type)
                        ? new ClientEmpresa(c.name, c.email, c.id, cashier)
                        : new Client(c.name, c.email, c.id, cashier);

                uc.addClient(client);
            }
        }
    }
}
