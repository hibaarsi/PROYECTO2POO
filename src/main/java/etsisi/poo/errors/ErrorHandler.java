package etsisi.poo.errors;

public class ErrorHandler {
    public static String format(Throwable  e) {
        if (e instanceof AppException) {
            return "[ERROR] " + e.getMessage() + "\n";
        }
        return "[ERROR] Error inesperado.\n";
    }
}
