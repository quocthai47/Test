package main.client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClientFileService {
    private static final int BUFFER_SIZE = 4096;
    private static final String downloadDirectory = "download";

    public String receiveFile(DataInputStream dis, long fileSize) throws IOException {
        File file = null;
        try {
            System.out.println("Starting download file \n");
            int bytesRead = 0, current = 0;
            byte[] byteArray = new byte[(int) fileSize];
            BufferedInputStream bis = new BufferedInputStream(dis);
            bytesRead = bis.read(byteArray, 0, byteArray.length);
            current = bytesRead;

            do {
                float percent = (current * 100L) / byteArray.length;
                System.out.println("Downloading Percent : " + percent + "%");
                bytesRead = bis.read(byteArray, current, (byteArray.length - current));
                if (bytesRead >= 0) current += bytesRead;
            } while (bytesRead > 0);

            file = createTempZipFile();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(byteArray, 0, current);
            bos.close();
            System.out.println("Files Successfully Downloaded!");
        } catch (IOException e) {
            Files.deleteIfExists(Paths.get(file.getPath()));
            throw e;
        }
        return file.getName();
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
                String filePath = downloadDirectory + File.separator + entry.getName();
                extractFile(zipIn, filePath);
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        } catch (EOFException e) {
            zipIn.close();
        } finally {
            Files.deleteIfExists(Paths.get(zipFilePath));
        }
    }

    public void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
    }

    private File createTempZipFile() {
        String fileName = "temp" + ThreadLocalRandom.current().nextInt(1, 1000) + ".zip";
        File clientTempDir = new File(downloadDirectory);
        if (!clientTempDir.exists()) {
            clientTempDir.mkdir();
        }
        return new File(downloadDirectory + File.separator + fileName);
    }

    private String getTempZipFilePath(String tempZipFileName) {
        return downloadDirectory + File.separator + tempZipFileName;
    }
}
