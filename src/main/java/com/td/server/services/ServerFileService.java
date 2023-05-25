package com.td.server.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.td.server.configuration.FileConfiguration.BUFFER_SIZE;
import static com.td.server.configuration.FileConfiguration.TEMP_DIRECTORY;

public class ServerFileService {
    private static Logger log = Logger.getLogger(ServerFileService.class.getName());
    private final String tempFileName = "compressed.zip";
    private String fileSystemDirectory;

    public ServerFileService(String fileSystemDirectory) {
        this.fileSystemDirectory = fileSystemDirectory;
    }

    public File[] listFiles() {
        return new File(fileSystemDirectory).listFiles();
    }

    public String formatFilesToString(File[] files) {
        StringBuffer sb = new StringBuffer();
        int k = 0;
        sb.append("\n        +---------+----------------------+\n");
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format("        | %-7s | %-20s |\n", "Sr No", "Filename");
        sb.append("        +---------+----------------------+\n");

        for (File f : files) {
            if (!f.isDirectory())
                formatter.format("        | %-7s | %-20s |\n", ++k, f.getName());
        }

        sb.append("        +---------+----------------------+\n\n        ");
        formatter.close();
        return sb.toString();
    }

    public String zipFiles(List<String> fileNames, String tempFilePath) throws IOException {
        File tempDir = new File(TEMP_DIRECTORY);
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        final FileOutputStream fos = new FileOutputStream(tempFilePath);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        for (String fName : fileNames) {
            try {
                File fileToZip = new File(getFilePath(fName));
                log.info("Finding file to send main.client: " + fName);

                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;

                log.info("Zipping file to send main.client: " + fName);
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();

            } catch (FileNotFoundException e) {
                String msg = "File " + fName + " Not Found! \n  ";
                Files.deleteIfExists(Paths.get(tempFilePath));
                log.log(Level.SEVERE, msg);
                throw new FileNotFoundException(msg);
            } finally {
                zipOut.closeEntry();
            }
        }

        zipOut.close();
        return tempFilePath;
    }

    public void sendFiles(DataOutputStream dos, String zipFilePath) throws IOException {
        log.info("Sending file to client");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zipFilePath));
        BufferedOutputStream bos = new BufferedOutputStream(dos);

        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            int numBytes;
            while ((numBytes = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, numBytes);
            }

        } finally {
            bos.flush();
            bis.close();
        }
        log.info("Successful sent file to client.");
    }

    public long getFileSize(String filePath) throws IOException {
        return Files.size(Paths.get(filePath));
    }

    public String getTempFilePath() {
        return new StringBuffer(TEMP_DIRECTORY)
                .append(File.separator)
                .append(ThreadLocalRandom.current().nextInt(1, 1000))
                .append(tempFileName)
                .toString();
    }

    private String getFilePath(String fileName) {
        return new StringBuffer(fileSystemDirectory)
                .append(File.separator)
                .append(fileName)
                .toString();
    }

}
