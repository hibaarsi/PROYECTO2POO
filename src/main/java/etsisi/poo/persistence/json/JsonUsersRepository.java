package etsisi.poo.persistence.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import etsisi.poo.errors.PersistenceException;
import etsisi.poo.persistence.dto.UsersDTO;
import etsisi.poo.persistence.repo.UsersRepository;

import java.io.IOException;
import java.nio.file.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUsersRepository implements UsersRepository {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path file = Paths.get("data", "users.json");

    private static final Logger log = LoggerFactory.getLogger(JsonUsersRepository.class);

    @Override
    public UsersDTO load() {
        ensureFolder();
        if (!Files.exists(file)) return new UsersDTO();

        try {
            String json = Files.readString(file);
            UsersDTO dto = gson.fromJson(json, UsersDTO.class);
            return (dto != null) ? dto : new UsersDTO();

        } catch (JsonSyntaxException e) {
            log.warn("users.json corrupto", e);
            Path bak = backupCorruptedWithTimestamp();
            throw new PersistenceException(
                    "users.json está corrupto. Se ha renombrado a " + bak.getFileName()
                            + ". Se arrancará sin usuarios."
            );

        } catch (IOException e) {
            log.error("No se pudo leer users.json", e);
            throw new PersistenceException("No se pudo leer users.json. Se arrancará sin usuarios.");
        }
    }

    @Override
    public void save(UsersDTO data) {
        ensureFolder();
        Path tmp = Paths.get("data", "users.tmp");

        try {
            String json = gson.toJson(data);
            Files.writeString(tmp, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            log.error("Error guardando users.json", e);
            throw new PersistenceException("No se pudieron guardar los usuarios. Cambios no persistidos.", e);
        }
    }

    private void ensureFolder() {
        try { Files.createDirectories(file.getParent()); }
        catch (IOException e) {
            log.warn("No se pudo crear el directorio de datos", e);
        }
    }

    private Path backupCorruptedWithTimestamp() {
        try {
            String ts = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            Path bak = Paths.get("data", "users.json.corrupt-" + ts + ".bak");
            return Files.move(file, bak, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("No se pudo renombrar users.json corrupto", e);
            return Paths.get("data", "users.json.bak");
        }
    }
}
