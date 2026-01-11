package etsisi.poo;

import etsisi.poo.errors.AppException;
import etsisi.poo.errors.ValidationException;

public abstract class Product implements TicketItem {
    //Constantes públicas
    public static final int MAX_NAME_LENGTH = 100;

    //Mensajes de validación
    private static final String NEEDS_TO_BE_POSITIVE = "It needs to be a positive number";
    private static final String NOT_EMPTY = "It can't be empty";
    private static final String SIZE_LIMIT_MESSAGE = "No more than 100 characters";
    private static final String PRICE_RESTRICTION = "Be greater than 0";

    //Mensajes de formato
    private static final String PRODUCT_FORMAT = "{class:Product, id:%d, name:'%s', category:%s, price:%.1f}";

    protected final int id; // es único
    protected String name;
    protected double price;

    public Product(int id, String name, double price) throws ValidationException {
        if (id < 0) throw new ValidationException(NEEDS_TO_BE_POSITIVE);
        if (name == null || name.trim().isEmpty()) throw new ValidationException(NOT_EMPTY);
        if (name.length() > MAX_NAME_LENGTH) throw new ValidationException(SIZE_LIMIT_MESSAGE);
        if (price <= 0) throw new ValidationException(PRICE_RESTRICTION);
        this.id = id;
        this.name = name.trim();
        this.price = price;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    } //mirar
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Category getCategory() {
        return null;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) System.out.println(NOT_EMPTY);
        if (name.length() > MAX_NAME_LENGTH) System.out.println(SIZE_LIMIT_MESSAGE);
        this.name = name.trim();
    }


    public void setPrice(double price) {
        if (price <= 0) System.out.println(PRICE_RESTRICTION);
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product producto = (Product) o;
        return id == producto.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        String cleanName = name.replace("\"", ""); // elimina comillas dobles
        return String.format(PRODUCT_FORMAT, id, cleanName, price);
    }
}