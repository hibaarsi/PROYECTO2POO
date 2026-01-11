package etsisi.poo.Commands.ProdCommands;

import etsisi.poo.Catalog;
import etsisi.poo.Commands.ICommand;
import etsisi.poo.Product;
import etsisi.poo.errors.ErrorHandler;

import java.util.*;

public class ProdListCommand implements ICommand {
    private final Catalog catalog;

    public ProdListCommand(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public String getPrimerArgumento() {
        return "prod";
    }

    @Override
    public String getSegundoArgumento() {
        return "list";
    }

    @Override
    public String execute(String[] args) {
        try{
            Map<Integer, Product> products = catalog.getProducts(); //copia del mapa de productos

            if (products.isEmpty()) {
                return "There are no products in the catalog.\n";

            }

            StringBuilder sb = new StringBuilder();
            sb.append("Catalog:\n");

            //el TreeMap ordena automáticamente las entradas por la clave (ID del producto).
            TreeMap<Integer, Product> organizedProducts = new TreeMap<>(products);

            for (Product p : organizedProducts.values()) {
                sb.append("  ").append(p).append("\n");
            }

            sb.append("prod list: ok\n");
            return sb.toString();
        } catch (Exception e){
            return ErrorHandler.format(e);
        }

        /*else {
            System.out.println("Catalog: ");
            //el TreeMap ordena automáticamente las entradas por la clave (ID del producto).
            TreeMap<Integer, Product> organizedProducts = new TreeMap<>(products);

            for (Product p : organizedProducts.values()) {
                System.out.println("  " + p);
            }
            System.out.println("prod list: ok\n");
        }
        return null;*/
    }

}
