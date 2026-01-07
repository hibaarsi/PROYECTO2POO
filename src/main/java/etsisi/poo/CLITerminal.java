package etsisi.poo;

import etsisi.poo.Commands.*;
import etsisi.poo.Commands.CashCommands.CashAddCommand;
import etsisi.poo.Commands.CashCommands.CashListCommand;
import etsisi.poo.Commands.CashCommands.CashRemoveCommand;
import etsisi.poo.Commands.CashCommands.CashTicketsCommand;
import etsisi.poo.Commands.ClientCommands.ClientAddCommand;
import etsisi.poo.Commands.ClientCommands.ClientListCommand;
import etsisi.poo.Commands.ClientCommands.ClienteRemoveCommand;
import etsisi.poo.Commands.ProdCommands.*;
import etsisi.poo.Commands.TicketCommands.*;
import etsisi.poo.errors.PersistenceException;
import etsisi.poo.persistence.PersistenceManager;
import etsisi.poo.persistence.json.JsonCatalogRepository;
import etsisi.poo.persistence.json.JsonTicketsRepository;
import etsisi.poo.persistence.json.JsonUsersRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class CLITerminal implements CLI {

    private CommandController commandController;
    private UserController userController;
    private TicketController ticketController;
    private Catalog catalog;
    private final static String PROMPT = "tUPM> ";

    private final Scanner sc;   //NUEVO


    public CLITerminal() {
        this.commandController = new CommandController(this);
        this.userController = new UserController();
        this.ticketController = new TicketController(userController);
        this.catalog = new Catalog();
        this.sc = new Scanner(System.in);  //NUEVO

        PersistenceManager pm = new PersistenceManager(
                new JsonUsersRepository(),
                new JsonCatalogRepository(),
                new JsonTicketsRepository()
        );

        // LOAD al arrancar
        try {
            pm.loadAll(userController, ticketController, catalog);
            printString("[OK] Datos cargados.\n");
        } catch (PersistenceException e) {
            printString("[WARN] " + e.getMessage() + "\n");
        }

        // SAVE al cerrar
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                pm.saveAll(userController, ticketController, catalog);
            } catch (Exception e) {
                System.err.println("[WARN] No se pudieron guardar los datos al salir.");
            }
        }));

        registerCommands();
    }

    public void run() {
        //Scanner sc = new Scanner(System.in);
        boolean keepRunning = true;

        while (keepRunning) {
            //System.out.print(PROMPT);
            //String command = sc.nextLine();
            printPrompt(); //NUEVO (quito todos los System.out lo imprimetodo la interfaz)
            String command = getCommand();

            String[] args = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            String primerArgumento = args[0];
            String segundoArgumento;

            if (args.length > 1) {
                segundoArgumento = args[1];
            } else {
                segundoArgumento = null;
            }

            keepRunning = startCommand(primerArgumento, segundoArgumento, args);
        }
        //sc.close();
    }

    public void runFromFile(String fileName) {
        try (FileReader fileReader = new FileReader(fileName);
             BufferedReader bufreader = new BufferedReader(fileReader)) {

            String command;
            while ((command = bufreader.readLine()) != null) {
                //System.out.print(PROMPT);
                //System.out.println(command);
                printString(PROMPT + command + "\n"); //NUEVO
                String[] args = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                String primerArgumento = args[0];
                String segundoArgumento;

                if (args.length > 1) {
                    segundoArgumento = args[1];
                } else {
                    segundoArgumento = null;
                }

                startCommand(primerArgumento, segundoArgumento, args);
            }
        } catch (IOException e) {
            printString("No encontrado tal archivo\n");
        }
    }

    //NUEVO
    //Interfaz CLI
    @Override
    public void printString(String message) {
        System.out.println(message);
    }

    @Override
    public String getCommand() {
        return sc.nextLine();
    }

    private void printPrompt() {
        System.out.print(PROMPT);
    }

    //Command Pattern
    public boolean startCommand(String primerArgumento, String segundoArgumento, String[] args) {
        return commandController.executeCommand(primerArgumento, segundoArgumento, args);
    }

    private void registerCommands() {
        commandController.registerCommand(new ProdAddCommand(catalog));
        commandController.registerCommand(
                new ProdAddEventCommand(catalog, EventType.FOOD, "addFood"));
        commandController.registerCommand(
                new ProdAddEventCommand(catalog, EventType.MEETING, "addMeeting"));
        commandController.registerCommand(new ProdListCommand(catalog));
        commandController.registerCommand(new ProdRemoveCommand(catalog));
        commandController.registerCommand(new ProdUpdateCommand(catalog));

        commandController.registerCommand(new CashAddCommand(userController));
        commandController.registerCommand(new CashRemoveCommand(userController, ticketController));
        commandController.registerCommand(new CashListCommand(userController));
        commandController.registerCommand(new CashTicketsCommand(this.userController));

        commandController.registerCommand(new ClientAddCommand(userController));
        commandController.registerCommand(new ClienteRemoveCommand(userController));
        commandController.registerCommand(new ClientListCommand(userController));
        commandController.registerCommand(new EchoCommand());
        commandController.registerCommand(new HelpCommand());
        commandController.registerCommand(new ExitCommand());

        commandController.registerCommand(new TicketNewCommand(this.ticketController));
        commandController.registerCommand(new TicketListCommand(this.ticketController));
        commandController.registerCommand(new TicketAddCommand(this.ticketController, this.userController, this.catalog));
        commandController.registerCommand(new TicketPrintCommand(this.ticketController));
        commandController.registerCommand(new TicketRemoveCommand(this.ticketController, this.userController, this.catalog));
    }

}
