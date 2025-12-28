package etsisi.poo.Commands;

import etsisi.poo.CLI;

import java.util.Arrays;

public class EchoCommand implements ICommand {
    @Override
    public String getPrimerArgumento() {
        return "echo";
    }

    @Override
    public String getSegundoArgumento() {
        return null;
    }

    @Override
    public String execute(String[] args) {
        if (args.length > 1) {
            String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            CLI.printFromString(text);
            System.out.println();

        } else {
            System.out.println("echo \"\"");
        }
        return null;
    }
}
