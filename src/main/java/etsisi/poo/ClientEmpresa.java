package etsisi.poo;

public class ClientEmpresa extends Client{
    public ClientEmpresa(String name, String email, String nif, Cashier cashier) {
        super(name, email, nif, cashier); // el id es el nif ya que es un cliente empresa
    }
    //arreglar client add

    @Override
    public String toString() {
        return String.format(
                "USER{identifier='%s', name='%s', email='%s', cash=%s}",
                this.id, getName(), getEmail(), this.cashier.getID());
    }
}
