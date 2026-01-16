package etsisi.poo.persistence.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import etsisi.poo.errors.PersistenceException;
import etsisi.poo.persistence.dto.CatalogDTO;
import etsisi.poo.persistence.repo.CatalogRepository;

import java.io.IOException;
import java.nio.file.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonCatalogRepository implements CatalogRepository {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path file = Paths.get("data", "catalog.json");

    private static final Logger log = LoggerFactory.getLogger(JsonCatalogRepository.class);

    @Override
    public CatalogDTO load() {
        ensureFolder();
        if (!Files.exists(file)) return new CatalogDTO();

        try {
            String json = Files.readString(file);
            CatalogDTO dto = gson.fromJson(json, CatalogDTO.class);
            if (dto != null) {
                return dto;
            }
            return new CatalogDTO();

        } catch (JsonSyntaxException e) {
            log.warn("catalog.json corrupto", e);
            Path bak = backupCorruptedWithTimestamp();
            throw new PersistenceException(
                    "catalog.json está corrupto. Se ha renombrado a " + bak.getFileName()
                            + ". Se arrancará sin catálogo."
            );

        } catch (IOException e) {
            log.error("No se pudo leer catalog.json", e);
            throw new PersistenceException("No se pudo leer catalog.json. Se arrancará sin catálogo.");
        }
    }

    @Override
    public void save(CatalogDTO data) {
        ensureFolder();
        Path tmp = Paths.get("data", "catalog.tmp");

        try {
            String json = gson.toJson(data);
            Files.writeString(tmp, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            log.error("Error guardando catalog.json", e);
            throw new PersistenceException("No se pudo guardar catalog.json. Cambios no persistidos.", e);
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
            Path bak = Paths.get("data", "catalog.json.corrupt-" + ts + ".bak");
            return Files.move(file, bak, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("No se pudo renombrar catalog.json corrupto", e);
            return Paths.get("data", "catalog.json.bak");
        }
    }
}
