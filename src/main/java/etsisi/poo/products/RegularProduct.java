package etsisi.poo.products;

public class RegularProduct extends Product {
    private Category category;

    public RegularProduct(int id, String name, Category category, double price) {
        super(id, name, price);
        this.category = category;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US,
                "{class:Product, id:%d, name:'%s', category:%s, price:%.1f}",
                id, name.replace("\"", ""), category, price);
    }
}
