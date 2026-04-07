package org.example.extract;

import java.util.Optional;

public interface CheckpointManager {

    public Optional<Long> loadCheckpoint();

    public void saveCheckpoint(long lineNumber);

    public void clear();

}