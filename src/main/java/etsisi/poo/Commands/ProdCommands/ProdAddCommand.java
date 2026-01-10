package etsisi.poo.Commands.ProdCommands;

import etsisi.poo.*;

import java.time.LocalDate;


public class ProdAddCommand extends AbstractProdAddCommand {
    public ProdAddCommand(Catalog catalog) {
        super(catalog);
    }

    @Override
    public String getPrimerArgumento() {
        return "prod";
    }

    @Override
    public String getSegundoArgumento() {
        return "add";
    }
    public String execute(String[] args) {
        if( args.length==4) {
            LocalDate date = LocalDate.parse(args[2]);
            ServiceCategory category = ServiceCategory.valueOf(args[3].toUpperCase());
            Service service = new Service(category, date);
            catalog.addService(service);
            return service.toString();

        }
        return super.execute(args);

    }
    @Override
    protected Product createProduct(String[] args) {
        int id;
        if(args.length!= 5 && args.length!= 6){
            System.out.println("Usage: prod add [<id>] \"<name>\" <category> <price> [<maxPersonal>]");
        }
        if(args.length ==5){
            id=0;
            String name=args[2];
            Category category = Category.valueOf(args[3].toUpperCase());
            double price = parsePrice(args[4]);
            return new RegularProduct(id,name, category, price);
        }else
        id = parseId(args[2]);
        String name = parseName(args[3]);
        Category category = Category.valueOf(args[4].toUpperCase());
        double price = parsePrice(args[5]);

        if (args.length == 7) { //si es personalizable
            int maxPersonal = parseId(args[6]);
            return new ProductPersonalized(id, name, category, price, maxPersonal);
        }

        return new RegularProduct(id, name, category, price);
    }

    @Override
    protected String getOkMessage() {
        return "prod add: ok\n";
    }
}
