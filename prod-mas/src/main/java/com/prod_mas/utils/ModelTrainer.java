package com.prod_mas.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ModelTrainer {
    @SuppressWarnings("CallToPrintStackTrace")
    public void startTraining(String modelScriptPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(modelScriptPath);
            pb.redirectErrorStream();
            Process p = pb.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("[TRAINER] " + line);
            }

            int exitCode = p.waitFor();
            if (exitCode == 0) {
                System.out.println("Model training completed successfully.");
            } else {
                System.err.println("Training failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
