package etsisi.poo.persistence.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import etsisi.poo.errors.PersistenceException;
import etsisi.poo.persistence.dto.TicketsDTO;
import etsisi.poo.persistence.repo.TicketsRepository;

import java.io.IOException;
import java.nio.file.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonTicketsRepository implements TicketsRepository {

    //EXPLICACION: JSON es un formato de texto estándar para representar datos.
    // Sirve para la persistencia local: guardar en fichero lo que hay en memoria.

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path file = Paths.get("data", "tickets.json");

    private static final Logger log = LoggerFactory.getLogger(JsonTicketsRepository.class);


    //Si los tickets se corrompen, arrancamos sin tickets pero sin bloquear el sistema.
    //Porque perder usuarios o catálogo invalida el sistema, mientras que perder tickets es recuperable.

    //En el caso de tickets.json se ha decidido una política más tolerante:
    //ante corrupción se realiza backup y se arranca sin tickets,
    //sin mostrar warning, ya que los tickets no son datos críticos y pueden
    //regenerarse durante la ejecución. Esto evita detener el flujo del programa
    //por información no esencial.

    @Override
    public TicketsDTO load() {
        ensureFolder();
        if (!Files.exists(file)) return new TicketsDTO();

        try {
            String json = Files.readString(file);
            TicketsDTO dto = gson.fromJson(json, TicketsDTO.class);
            return (dto != null) ? dto : new TicketsDTO();
        } catch (JsonSyntaxException e) {
            log.warn("tickets.json corrupto: se hará backup y se arrancará sin tickets", e);
            backupCorrupted();
            return new TicketsDTO();

        } catch (IOException e) {
            log.error("No se pudo leer tickets.json: se arrancará sin tickets", e);
            return new TicketsDTO();
        }

    }

    @Override
    public void save(TicketsDTO data) {
        ensureFolder();
        Path tmp = Paths.get("data", "tickets.tmp");

        try {
            String json = gson.toJson(data);
            Files.writeString(tmp, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            log.error("No se pudieron guardar los tickets", e);
            throw new PersistenceException("No se pudieron guardar los tickets. Cambios no persistidos.", e);
        }
    }

    private void ensureFolder() {
        try {
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            log.warn("No se pudo crear el directorio de datos para tickets", e);
        }
    }

    private void backupCorrupted() {
        try {
            Path bak = Paths.get("data", "tickets.json.bak");
            Files.move(file, bak, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("No se pudo hacer backup de tickets.json corrupto", e);
        }
    }
}
