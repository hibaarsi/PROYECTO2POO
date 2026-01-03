package etsisi.poo;

public class App2 {
    public static void main(String[] args) {
        //CLI cli = new CLI();
        CLI cli = new CLITerminal();  //NUEVO
        if (args.length > 0) {
            cli.runFromFile(args[0]);
        } else {
            cli.run();
        }
    }
}
