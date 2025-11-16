package persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SerializableDao<T extends Serializable> implements DataAccessObject<T> {

    private final Path filePath;
    private final Class<T> type;

    public SerializableDao(String filePath, Class<T> type) {
        this.filePath = Paths.get(filePath);
        this.type = type;
        Path parent = this.filePath.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new RuntimeException("Error creando directorios para el archivo de persistencia", e);
            }
        }
    }

    @Override
    public void saveAll(List<T> items) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(filePath)))) {
            outputStream.writeObject(items);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar los datos en el archivo", e);
        }
    }

    @Override
    public List<T> loadAll() {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try {
            BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(filePath));
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object rawData = ois.readObject();
            if (!(rawData instanceof List<?> rawList)) {
                throw new RuntimeException("El contenido del archivo no es una lista");
            }

            List<T> typedList = new ArrayList<>(rawList.size());
            for (Object element : rawList) {
                if (element != null && !type.isInstance(element)) {
                    throw new RuntimeException("Elemento con tipo inesperado encontrado en la lista");
                }
                typedList.add(type.cast(element));
            }

            return typedList;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar los datos del archivo", e);
        }
    }
}
