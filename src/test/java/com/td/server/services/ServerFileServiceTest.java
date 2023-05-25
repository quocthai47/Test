package com.td.server.services;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.td.server.configuration.FileConfiguration.TEMP_DIRECTORY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerFileServiceTest {
    private static String testDirectory = "test";
    private static String testFileName = "a";
    private static String pathToTestFile = new StringBuffer("test").append(File.separator).append("a").toString();
    private ServerFileService service = new ServerFileService(testDirectory);

    @BeforeAll
    private static void setup() throws IOException {
        if (!new File(testDirectory).exists()) {
            Files.createDirectory(Paths.get(testDirectory));
        }

        if (!new File(pathToTestFile).exists()) {
            Files.createFile(Paths.get(pathToTestFile));
        }
    }

    @AfterAll
    private static void clean() throws IOException {
        Files.deleteIfExists(Paths.get(pathToTestFile));
        Files.deleteIfExists(Paths.get(testDirectory));
    }

    @DisplayName("Test display all files for index command")
    @Test
    public void testListFile() {
        //when
        File[] files = service.listFiles();

        Assertions.assertEquals(files.length, 1);
        Assertions.assertEquals(files[0].getName(), testFileName);
    }

    @DisplayName("Test display all files for index command")
    @Test
    public void testFormatFilesToString() {
        //given
        File[] files = service.listFiles();
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

        //when
        String result = service.formatFilesToString(files);

        //then
        Assertions.assertEquals(result, sb.toString());
    }

    @Test
    public void testZipFiles() throws IOException {
        //given
        String tempFilePath = service.getTempFilePath();
        File[] files = service.listFiles();

        //when
        String result = service.zipFiles(Arrays.stream(files).map(File::getName).collect(Collectors.toList()), tempFilePath);

        Set<String> zipFiles =
                Stream.of(new File(TEMP_DIRECTORY).listFiles())
                        .map(File::getName)
                        .collect(Collectors.toSet());
        //then
        Assertions.assertTrue(result.contains("compressed.zip"));
        Assertions.assertEquals(zipFiles.size(), 1);

        //clean
        Files.deleteIfExists(Paths.get(result));
        Files.deleteIfExists(Paths.get(TEMP_DIRECTORY));
    }

    @Test
    public void testZipFilesFileNotFoundToZip() throws IOException {
        //given
        String tempFilePath = service.getTempFilePath() + "notFoundPath";

        //when
        Exception exception = assertThrows(FileNotFoundException.class, () -> {
            service.zipFiles(List.of("NotFoundFile"), tempFilePath);
        });

        //then
        assertEquals("File NotFoundFile Not Found! \n  ", exception.getMessage());

    }

    @Test
    public void testGetFileSize() throws IOException {
        //given
        String path = "src/test/resources/files/1.pdf";

        //when
        long fileSize = service.getFileSize(path);

        //then
        Assertions.assertEquals(111210L, fileSize);

    }

//    @Test
//    public void testSendFiles() throws IOException {
//        //given
//        String path = "src/test/resources/files/1.pdf";
//        FileOutputStream sentOutputStream = new FileOutputStream("sentOutputStream");
//
//        FileOutputStream receivedOutputStream = new FileOutputStream("receivedOutputStream");
//        DataOutputStream dos = new DataOutputStream(receivedOutputStream);
//
//        //when
//        service.sendFiles(dos, path);
//
//        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
//        BufferedOutputStream bos = new BufferedOutputStream(sentOutputStream);
//
//        byte[] buffer = new byte[BUFFER_SIZE];
//        try {
//            int numBytes;
//            while ((numBytes = bis.read(buffer)) != -1) {
//                bos.write(buffer, 0, numBytes);
//            }
//
//        } finally {
//            bos.flush();
//            bis.close();
//            bos.close();
//        }
//
//        Assertions.assertEquals(receivedOutputStream.hashCode(), sentOutputStream.hashCode());
//    }


}
