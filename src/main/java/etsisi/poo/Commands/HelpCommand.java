package etsisi.poo.Commands;

public class HelpCommand implements ICommand {
    private static final String SPACE = "    ";
    private static final String HELP_CATEGORIES =
            "Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS \n";
    private static final String HELP_DISCOUNTS =
            "Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, \nELECTRONICS 3%.\n";

    @Override
    public String getPrimerArgumento() {
        return "help";
    }

    @Override
    public String getSegundoArgumento() {
        return null;
    }

    @Override
    public String execute(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("Commands:\n")
                .append(SPACE).append("client add \"<nombre>\" <DNI> <email> <cashId>\n")
                .append(SPACE).append("client remove <DNI>\n")
                .append(SPACE).append("client list\n")
                .append(SPACE).append("cash add [<id>] \"<nombre>\"<email>\n")
                .append(SPACE).append("cash remove <id>\n")
                .append(SPACE).append("cash list\n")
                .append(SPACE).append("cash tickets <id>\n")
                .append(SPACE).append("ticket new [<id>] <cashId> <userId>\n")
                .append(SPACE).append("ticket add <ticketId><cashId> <prodId> <amount> [--p<txt> --p<txt>]\n")
                .append(SPACE).append("ticket remove <ticketId><cashId> <prodId>\n")
                .append(SPACE).append("ticket print <ticketId> <cashId>\n")
                .append(SPACE).append("ticket list\n")
                .append(SPACE).append("prod add <id> \"<name>\" <category> <price>\n")
                .append(SPACE).append("prod update <id> NAME|CATEGORY|PRICE <value>\n")
                .append(SPACE).append("prod addFood [<id>] \"<name>\" <price> <expiration:yyyy-MM-dd> <max_people>\n")
                .append(SPACE).append("prod addMeeting [<id>] \"<name>\" <price> <expiration:yyyy-MM-dd> <max_people>\n")
                .append(SPACE).append("prod list\n")
                .append(SPACE).append("prod remove <id>\n")
                .append(SPACE).append("help\n")
                .append(SPACE).append("echo \"<texto>\"\n")
                .append(SPACE).append("exit\n\n\n")
                .append(HELP_CATEGORIES)
                .append(HELP_DISCOUNTS);
        return sb.toString();
    }
}
