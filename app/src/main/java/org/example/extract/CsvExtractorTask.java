package org.example.extract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;

public class CsvExtractorTask implements Runnable {

    private final File inputCsv;
    private final BlockingQueue<String> loadQueue;
    private final CheckpointManager checkpointManager;
    private final String POISON_PILL;

    public CsvExtractorTask(File inputCsv,
                    BlockingQueue<String> loadQueue,
                    CheckpointManager checkpointManager,
                    String poisonPill) {
        this.inputCsv = inputCsv;
        this.loadQueue = loadQueue;
        this.checkpointManager = checkpointManager;
        this.POISON_PILL = poisonPill;
    }

    @Override
    public void run() {
        long currentLine = 0;
        long startLine = checkpointManager.loadCheckpoint().orElse(0L);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputCsv))) {
            String line;
            while ((line = reader.readLine()) != null) {

                if (currentLine < startLine) {
                    currentLine++;
                    continue;
                }

                // Backpressure: se la coda è piena, questa put() blocca
                loadQueue.put(line);

                if (currentLine % 1000 == 0) {
                    checkpointManager.saveCheckpoint(currentLine);
                }

                currentLine++;
            }

            // Fine: manda la poison pill per i consumer
            loadQueue.put(POISON_PILL);

            // opzionale: checkpoint finale
            checkpointManager.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}