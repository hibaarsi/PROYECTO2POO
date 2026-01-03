package etsisi.poo;

public interface CLI {

    /*Poner el CLI como una interfaz y CLITerminal como una clase que la implementa
    permite desacoplar la lógica de la aplicación del mecanismo de entrada y salida.
    mejora la reutilización del código a parte de las dependencias y permite cambiar
    la forma de interactuar con el usuario sin afectar al resto de la aplicación.*/

    void run();

    void runFromFile(String fileName);

    void printString(String message);

    String getCommand();

}