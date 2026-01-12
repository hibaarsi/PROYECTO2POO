package etsisi.poo.Commands;

import etsisi.poo.CLITerminal;
import etsisi.poo.errors.AppException;
import etsisi.poo.errors.ErrorHandler;

import java.util.HashMap;
import java.util.Map;

public class CommandController {
    private Map<String, ICommand> commands = new HashMap<>();

    //NUEVO
    private CLITerminal cli;

    public CommandController(CLITerminal cli) {
        this.cli = cli;
    }

    public boolean executeCommand(String primerArgumento, String segundoArgumento, String[] args) {
        ICommand command = null;

        if (segundoArgumento != null) {
            command = commands.get(primerArgumento + ":" + segundoArgumento);
        }

        if (command == null) {
            command = commands.get(primerArgumento);
        }

        if (command == null) {
            cli.printString("Comando no encontrado\n");
            return true;
        }

        try {
            String result = command.execute(args);
            if (result != null) {
                cli.printString(result);
            }
            return !primerArgumento.equals("exit");

        } catch (AppException e){
            // Captura de las validaciones
            cli.printString(ErrorHandler.format(e));
            return true;
        } catch (Exception e) {
            // Captura para imprevistos
            cli.printString(ErrorHandler.format(e));
            return true;
        }
    }


    public void registerCommand(ICommand command) {
        //para que identifique la primera y segunda parte del comando
        String key;
        if (command.getSegundoArgumento() == null) {
            key = command.getPrimerArgumento();

        } else {
            key = command.getPrimerArgumento() + ":" + command.getSegundoArgumento();
        }
        commands.put(key, command);
    }

   /* public boolean executeCommand(String primerArgumento, String segundoArgumento, String[] args) {
        ICommand command = null;
        String key;
        //primero se intenta con un comando de dos args
        if (segundoArgumento != null) {
            key = primerArgumento + ":" + segundoArgumento;
            command = commands.get(key);
        }

        //si no va intentamos un comando de un arg
        if (command == null) {
            key = primerArgumento;
            command = commands.get(key);
        }

        //si sigue sin ir pues es que no es un comando bien escrito
        if (command == null) {
            System.out.println("Comando no encontrado\n");
            return true; //para que la app se siga ejecutando
        }

        String comandoExecute = command.execute(args);
        if (comandoExecute != null) {
            System.out.println(comandoExecute);
        }

        //salir si el usuario ha escrito exit
        boolean exit = primerArgumento.equals("exit");
        return !exit;
    }*/
}
