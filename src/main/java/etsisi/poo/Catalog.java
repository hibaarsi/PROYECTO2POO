package etsisi.poo;


import java.util.HashMap;

import java.util.Map;

public class Catalog {
    private final Map<Integer, Product> items;
    private final Map<String, Service> services;
    private static final int MAX_ELEMENTS = 200;

    public Catalog() {
        this.items = new HashMap<>();
        this.services = new HashMap<>();
    }

    public boolean addProduct(Product product) {
        if (items.size() >= MAX_ELEMENTS) {
            return false; // límite alcanzado
        }
        if (items.containsKey(product.getId())) {
            return false; //  esta duplicado
        }
        items.put(Integer.valueOf(product.getId()), product);
        return true;
    }
    public void addService(Service service) {
        services.put(service.getId(), service);
    }
    public void removeService(String id) {
        services.remove(id);
    }
    public Service getService(String id) {
        return services.get(id);
    }

    public Map<Integer, Product> getProducts() {
        return new HashMap<>(items); //da una copia del mapa interno items,para protegerlo
    }

    public Product getProduct(int id) {
        return items.get(id);
    }

    public Product removeProduct(int id) {
        return items.remove(id);
    }

    public boolean updateProduct(int id, String field, String value) {
        Product product = items.get(id);
        if (product == null) {
            return false;
        }

        switch (field.toUpperCase()) {
            case "NAME":
                product.setName(value);
                return true;

            case "CATEGORY":
                if (product instanceof RegularProduct)
                    try {
                        Category category = Category.valueOf(value.toUpperCase());
                        ((RegularProduct) product).setCategory(category);
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false; // categoría inválida
                    }
                else return false;

            case "PRICE":
                try {
                    double price = Double.parseDouble(value);
                    if (price <= 0) {
                        return false;
                    }
                    product.setPrice(price);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }

            default:
                return false; // campo desconocido
        }
    }
}