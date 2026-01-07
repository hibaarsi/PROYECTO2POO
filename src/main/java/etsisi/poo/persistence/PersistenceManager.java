package etsisi.poo.persistence;

import etsisi.poo.*;
import etsisi.poo.errors.PersistenceException;
import etsisi.poo.persistence.dto.TicketsDTO;
import etsisi.poo.persistence.dto.UsersDTO;
import etsisi.poo.persistence.repo.TicketsRepository;
import etsisi.poo.persistence.repo.UsersRepository;
import etsisi.poo.persistence.dto.CatalogDTO;
import etsisi.poo.persistence.repo.CatalogRepository;

public class PersistenceManager {

    private final UsersRepository usersRepo;
    private final TicketsRepository ticketsRepo;
    private final CatalogRepository catalogRepo;

    private final UsersMapper usersMapper = new UsersMapper();
    private final TicketsMapper ticketsMapper = new TicketsMapper();
    private final CatalogMapper catalogMapper = new CatalogMapper();

    public PersistenceManager(UsersRepository usersRepo, CatalogRepository catalogRepo, TicketsRepository ticketsRepo) {
        this.usersRepo = usersRepo;
        this.catalogRepo = catalogRepo;
        this.ticketsRepo = ticketsRepo;
    }

    public void loadAll(UserController uc, TicketController tc, Catalog catalog) {
        try {
            UsersDTO users = usersRepo.load();
            usersMapper.toModel(users, uc);
        } catch (PersistenceException e) {
            // re-lanzar o dejar que el de arriba avise
            throw e;
        }

        try {
            CatalogDTO cat = catalogRepo.load();
            catalogMapper.toModel(cat, catalog);
        } catch (PersistenceException e) {
            throw e;
        }

        try {
            TicketsDTO tickets = ticketsRepo.load();
            ticketsMapper.toModel(tickets, tc, uc, catalog);
        } catch (PersistenceException e) {
            throw e;
        }
        /*UsersDTO users = usersRepo.load();
        usersMapper.toModel(users, uc);

        CatalogDTO cat = catalogRepo.load();
        catalogMapper.toModel(cat, catalog);

        TicketsDTO tickets = ticketsRepo.load();
        ticketsMapper.toModel(tickets, tc, uc, catalog);
        */

    }


    public void saveAll(UserController uc, TicketController tc, Catalog catalog) {
        UsersDTO users = usersMapper.fromModel(uc);
        usersRepo.save(users);

        CatalogDTO cat = catalogMapper.fromModel(catalog);
        catalogRepo.save(cat);

        TicketsDTO tickets = ticketsMapper.fromModel(tc, uc);
        ticketsRepo.save(tickets);
    }
}
