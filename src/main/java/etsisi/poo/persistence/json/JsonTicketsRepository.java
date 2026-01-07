package etsisi.poo.persistence.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import etsisi.poo.errors.PersistenceException;
import etsisi.poo.persistence.dto.TicketsDTO;
import etsisi.poo.persistence.repo.TicketsRepository;

import java.io.IOException;
import java.nio.file.*;

public class JsonTicketsRepository implements TicketsRepository {

    //EXPLICACION: JSON es un formato de texto estándar para representar datos.
    // Sirve para la persistencia local: guardar en fichero lo que hay en memoria.

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path file = Paths.get("data", "tickets.json");

    @Override
    public TicketsDTO load() {
        ensureFolder();
        if (!Files.exists(file)) return new TicketsDTO();

        try {
            String json = Files.readString(file);
            TicketsDTO dto = gson.fromJson(json, TicketsDTO.class);
            return (dto != null) ? dto : new TicketsDTO();
        } catch (JsonSyntaxException | IOException e) {
            backupCorrupted();
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
            throw new PersistenceException("No se pudieron guardar los tickets. Cambios no persistidos.", e);
        }
    }

    private void ensureFolder() {
        try {
            Files.createDirectories(file.getParent());
        } catch (IOException ignored) {
        }
    }

    private void backupCorrupted() {
        try {
            Path bak = Paths.get("data", "tickets.json.bak");
            Files.move(file, bak, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
        }
    }
}
