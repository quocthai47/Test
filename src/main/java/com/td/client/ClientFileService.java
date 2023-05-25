package com.td.client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.td.client.Configuration.BUFFER_SIZE;

public class ClientFileService {
    private String downloadDirectory;

    ClientFileService(String downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    public String receiveFile(DataInputStream dis, long fileSize) throws IOException {
        System.out.println("Starting download file \n");
        String fileName = createTempZipFileName();
        String filePath = createTempZipFile(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        BufferedInputStream bis = new BufferedInputStream(dis);
        try {
            byte[] bytes = new byte[BUFFER_SIZE];
            int length, current = 0;

            while (current < fileSize) {
                length = bis.read(bytes);
                current += length;
                bos.write(bytes, 0, length);
                float percent = (current * 100L) / fileSize;
                System.out.println("Downloading Percent : " + percent + "%");
            }

        } catch (IOException e) {
            Files.deleteIfExists(Paths.get(filePath));
            throw e;
        } finally {
            bos.flush();
        }
        System.out.println("Files Successfully Downloaded!");
        return fileName;
    }

    public void unzip(String tempZipFileName) throws IOException {
        File destDir = new File(downloadDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        String zipFilePath = getTempZipFilePath(tempZipFileName);
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        try {
            while (entry != null) {
                if(!entry.getName().contains("__MACOSX")) {
                    String filePath = downloadDirectory + File.separator + entry.getName();
                    extractFile(zipIn, filePath);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        } catch (EOFException e) {
            zipIn.close();
        } finally {
            Files.deleteIfExists(Paths.get(zipFilePath));
        }
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.flush();
    }

    private String createTempZipFileName() {
        return "temp" + ThreadLocalRandom.current().nextInt(1, 1000) + ".zip";
    }
    private String createTempZipFile(String fileName) {
        File clientTempDir = new File(downloadDirectory);
        if (!clientTempDir.exists()) {
            clientTempDir.mkdir();
        }
        return new StringBuffer(downloadDirectory).append(File.separator).append(fileName).toString();
    }

    private String getTempZipFilePath(String tempZipFileName) {
        return new StringBuffer(downloadDirectory).append(File.separator).append(tempZipFileName).toString();
    }
}
