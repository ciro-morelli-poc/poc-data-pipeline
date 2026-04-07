package org.example.load;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.BlockingQueue;

public class OutputFileLoaderTask implements Runnable {

    private final BlockingQueue<String> writeQueue;
    private final File outputFile;
    private final String poisonPill;

    public OutputFileLoaderTask(BlockingQueue<String> writeQueue,
                      File outputFile,
                      String poisonPill) {
        this.writeQueue = writeQueue;
        this.outputFile = outputFile;
        this.poisonPill = poisonPill;
    }

    @Override
    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            while (true) {
                String line = writeQueue.take();
                if (line.equals(poisonPill)) {
                    break;
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}