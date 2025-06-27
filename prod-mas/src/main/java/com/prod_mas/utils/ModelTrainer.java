package com.prod_mas.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ModelTrainer {
    @SuppressWarnings("CallToPrintStackTrace")
    public void startTraining(String url) {
        try {
            String filePath = this.getFilePath(url);
            if (filePath.isEmpty()) {
                throw new Exception("The File path is empty!");
            }

            ProcessBuilder pb = new ProcessBuilder(filePath);
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

            File file = new File(filePath);
            if (!file.delete()) {
                throw new Exception("Fail to delete exe file!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private String getFilePath(String urlFile) {
        try {
            String fileExtension = urlFile.substring(urlFile.lastIndexOf('.'));
            Path tempFile = Files.createTempFile("downloaded_", fileExtension);

            URL downloadUrl = new URL(urlFile);
            HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new IOException("Failed to download file. HTTP code: " + responseCode);
            }

            try (InputStream in = connection.getInputStream()) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return tempFile.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
