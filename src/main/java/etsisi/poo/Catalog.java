package etsisi.poo;


import etsisi.poo.errors.ValidationException;

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

        int key = Integer.parseInt(product.getId());
        if (items.containsKey(key)) return false;
        items.put(key, product);

        return true;
    }

    public boolean addService(Service service) {
        if (services.size() >= MAX_ELEMENTS) {
            return false;
        }
        if (services.containsKey(service.getId())) {
            return false;
        }
        services.put(service.getId(), service);
        return true;
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

    /*public boolean updateProduct(int id, String field, String value) {
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
    }*/
    public void updateProduct(int id, String field, String value) {
        Product product = items.get(id);

        // 1. Validación de existencia
        if (product == null) {
            throw new ValidationException("Product with id " + id + " does not exist");
        }

        // 2. Selección de campo
        switch (field.toUpperCase()) {
            case "NAME":
                if (value == null || value.trim().isEmpty()) {
                    throw new ValidationException("Name cannot be empty");
                }
                product.setName(value.replace("\"", ""));
                break;

            case "CATEGORY":
                // Validamos si este tipo de producto tiene categoría
                if (!(product instanceof RegularProduct)) {
                    throw new ValidationException("This product type does not support categories");
                }

                try {
                    Category category = Category.valueOf(value.toUpperCase());
                    ((RegularProduct) product).setCategory(category);
                } catch (IllegalArgumentException e) {
                    // Envolvemos el error técnico en uno legible
                    throw new ValidationException("Invalid category: " + value);
                }
                break;

            case "PRICE":
                try {
                    double price = Double.parseDouble(value);
                    if (price <= 0) {
                        throw new ValidationException("Enter a positive value for the price: " + value);
                    }
                    product.setPrice(price);
                } catch (NumberFormatException e) {
                    throw new ValidationException("Invalid price format: " + value);
                }
                break;

            default:
                throw new ValidationException("Unknown or uneditable field: " + field);
        }
    }

    public Map<String, Service> getServices() {
        return new HashMap<>(services);
    }

}