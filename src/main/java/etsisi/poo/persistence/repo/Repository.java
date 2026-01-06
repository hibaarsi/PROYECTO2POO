package etsisi.poo.persistence.repo;

public interface Repository<T> {

    //EXPLICACION: Un repositorio es la clase que se encarga de guardar y cargar datos,
    // pero el resto del programa no sabe si eso se guarda en JSON, base de datos, etc.
    //
    // Esto ayuda a separar responsabilidades, si te piden guardar en XML solo cambias el repo
    // y facilita testear

    T load();

    void save(T data);
}
