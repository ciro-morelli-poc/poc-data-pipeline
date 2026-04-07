package org.example.extract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class CheckpointManagerImpl implements CheckpointManager {

    private final File checkpointFile;

    public CheckpointManagerImpl(File checkpointFile) {
        this.checkpointFile = checkpointFile;
    }

    public Optional<Long> loadCheckpoint() {
        if (!checkpointFile.exists()) {
            return Optional.empty();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(checkpointFile))) {
            String line = reader.readLine();
            if (line != null) {
                return Optional.of(Long.parseLong(line.trim()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void saveCheckpoint(long lineNumber) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(checkpointFile))) {
            writer.write(Long.toString(lineNumber));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        checkpointFile.delete();
    }
}