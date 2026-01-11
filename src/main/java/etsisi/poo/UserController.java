package etsisi.poo;

import etsisi.poo.errors.ValidationException;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class UserController {
    private Map<String, Client> clientMap;     // La clave es el DNI
    private Map<String, Cashier> cashierMap;   // La clave es el código UW

    public Map<String, Client> getClientMap() { return clientMap; }
    public Map<String, Cashier> getCashierMap() { return cashierMap; }


    public UserController() {
        this.clientMap = new HashMap<>();
        this.cashierMap = new HashMap<>();
    }

    public Client getClient(String clientID) {
        return clientMap.get(clientID);
    }

    public Cashier getCashier(String cashierID) {
        return cashierMap.get(cashierID);
    }

    public Client createClient(String name, String email, String id, Cashier cashier) {
        if (clientMap.containsKey(id)) {throw new ValidationException("Ya hay un cliente con ese DNI");
        }

        if (!cashierMap.containsKey(cashier.getID())) {
            throw new ValidationException("El cajero no existe");
        }
        if (!properFormatID(id)) {
            throw new ValidationException("El ID no es correcto");
        }//poner q salte una excepcion
        //si el formato no es corecto se crea igual el objeto añadir return
        if (Character.isLetter(id.charAt(id.length()-1))) {
            return new Client(name, email, id, cashier);
        } else {
            return new ClientEmpresa(name, email, id, cashier);
        }
    }

    private boolean properFormatID(String id) {
        if (id == null || id.isEmpty()) return false;
        if (Character.isLetter(id.charAt(0))) {
            return id.length() == 9;
        }
        return Character.isDigit(id.charAt(0));
    }

    public void addClient(Client client) {
        if (client != null) {
            clientMap.put(client.getID(), client);
        }
    }

    public void removeClient(String DNI) {
        clientMap.remove(DNI);
    }

    public void removeTicketFromAnyClient(TicketModel ticket) {
        Iterator<Client> it = this.clientMap.values().iterator();
        boolean found = false;

        while (it.hasNext() && !found) {
            Client client = it.next();
            if (client.getTickets().remove(ticket)) {
                found = true;
            }
        }
    }

    public void listClients() {
        List<Client> sortedClients = getClientsSortedByName();
        System.out.println("Client:");
        for (Client c : sortedClients) {
            System.out.println(c);
        }
    }

    public List<Client> getClientsSortedByName() {
        List<Client> clientList = new ArrayList<>(this.clientMap.values());
        //Esto compara los clientes usando el .getName()
        clientList.sort(Comparator.comparing(Client::getName));
        return clientList;
    }

    public Cashier createCashier(String name, String email, String UW) {
        if (UW == null) {
            UW = generateCashierID();
            return new Cashier(name, email, UW);
        }

        if (!properFormatUW(UW)) {
            return null;
        }

        return new Cashier(name, email, UW);
    }

    public void addCashier(Cashier cashier) {
        if (cashier != null) {
            cashierMap.put(cashier.getID(), cashier);
        }
    }


    // Este removeCashier solo borra el cajero del mapa sin borrar los tickets asociados
    public void removeCashier(String UW) {
        if (!cashierMap.containsKey(UW)) {
            System.out.println("El cajero no está registrado.");
        } else {
            cashierMap.remove(UW);
        }
    }


    public void listCashier() {
        List<Cashier> sortedCashier = getCashiersSortedByName();
        System.out.println("Cash: ");
        for (Cashier c : sortedCashier) {
            System.out.println(" " + c);
        }
    }

    public List<Cashier> getCashiersSortedByName() {
        List<Cashier> cashierList = new ArrayList<>(this.cashierMap.values());
        cashierList.sort(Comparator.comparing(Cashier::getName));
        return cashierList;
    }

    public List<Cashier> getCashiersSortedByID() {
        List<Cashier> cashierList = new ArrayList<>(this.cashierMap.values());
        cashierList.sort(Comparator.comparing(Cashier::getID));
        return cashierList;
    }

    private String generateCashierID() {
        StringBuilder sb = new StringBuilder("UW");
        int numericPart = ThreadLocalRandom.current().nextInt(1000000, 10000000);
        sb.append(numericPart);
        return sb.toString();
    }

    private boolean properFormatUW(String UW) {
        if (!UW.startsWith("UW")) {
            return false;
        }

        if (UW.length() != 9) {
            return false;
        } else {
            String numericPart = UW.substring(2);
            for (Character c : numericPart.toCharArray()) {
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
        }

        return true;
    }

    public List<Cashier> listCashiersIt() {
        return new ArrayList<>(cashierMap.values());
    }

    public List<Client> listClientsIt() {
        return new ArrayList<>(clientMap.values());
    }

}