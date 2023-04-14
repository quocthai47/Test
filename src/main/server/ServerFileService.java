package main.server;

import java.io.*;
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

public class ServerFileService {
    private static Logger log = Logger.getLogger(ServerFileService.class.getName());
    private final String fileDirectory = "files";
    private final String tempDirectory = "temp";
    private final String tempFileName = "compressed";
    private final String zipExtension = ".zip";

    public StringBuffer listFiles(File[] files) {
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
        return sb;
    }

    public String zipFiles(List<String> fileNames, String tempFilePath) throws IOException {
        File tempDir = new File(tempDirectory);
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

                zipOut.closeEntry();
                fis.close();
                zipOut.close();
            } catch (FileNotFoundException e) {
                String msg = "File " + fName + " Not Found! \n  ";
                Files.deleteIfExists(Paths.get(tempFilePath));
                log.log(Level.SEVERE, msg);
                throw new FileNotFoundException(msg);
            }

        }
        return tempFilePath;
    }

    public void sendFiles(DataOutputStream dos, String zipFilePath) throws IOException {
        log.info("Sending file to main.client.");
        File file = new File(zipFilePath);
        byte[] byteArray = new byte[(int) file.length()];

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream bos = new BufferedOutputStream(dos);
        int count;
        while ((count = bis.read(byteArray)) != -1) {
            bos.write(byteArray, 0, count);
        }

        bos.flush();
        bis.close();
        log.info("Successful sent file to main.client.");
        Files.deleteIfExists(Paths.get(zipFilePath));
    }

    private String getFilePath(String fileName) {
        return new StringBuffer(fileDirectory)
                .append(File.separator)
                .append(fileName)
                .toString();
    }

    public String getTempFilePath() {
        return new StringBuffer(tempDirectory)
                .append(File.separator)
                .append(tempFileName)
                .append(ThreadLocalRandom.current().nextInt(1, 1000))
                .append(zipExtension)
                .toString();
    }
}
