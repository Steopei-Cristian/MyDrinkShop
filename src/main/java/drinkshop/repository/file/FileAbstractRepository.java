package drinkshop.repository.file;

import drinkshop.repository.AbstractRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class FileAbstractRepository<I, E>
        extends AbstractRepository<I, E> {

    protected String fileName;

    protected FileAbstractRepository(String fileName) {
        this.fileName = fileName;
        loadFromFile();
    }

    protected void loadFromFile() {
        Path path = Path.of(fileName);
        if (!Files.exists(path)) {
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(path)) {

            String line;
            while ((line = br.readLine()) != null) {
                E entity = extractEntity(line);
                super.save(entity);
            }

        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load file " + fileName, e);
        }
    }

    private void writeToFile() {
        Path path = Path.of(fileName);
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {

            for (E entity : entities.values()) {
                bw.write(createEntityAsString(entity));
                bw.newLine();
            }

        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write file " + fileName, e);
        }
    }

    @Override
    public E save(E entity) {
        E e = super.save(entity);
        writeToFile();
        return e;
    }

    @Override
    public E delete(I id) {
        E e = super.delete(id);
        writeToFile();
        return e;
    }

    @Override
    public E update(E entity) {
        E e = super.update(entity);
        writeToFile();
        return e;
    }

    protected abstract E extractEntity(String line);

    protected abstract String createEntityAsString(E entity);
}
