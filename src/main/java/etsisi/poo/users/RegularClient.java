package etsisi.poo.users;

public class RegularClient extends Client {
    public RegularClient(String name, String email, String dni, Cashier cashier) {
        super(name, email, dni, cashier); // el id es el dni ya que es un cliente normal
    }

    @Override
    public String toString() {
        return String.format(
                "RegularClient{identifier='%s', name='%s', email='%s', cash=%s}",
                this.id, getName(), getEmail(), this.cashier.getID());
    }
}
