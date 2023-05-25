package com.td.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientFileServiceTest {
    private String DOWNLOAD_DIRECTORY = "src/test/resources/download";
    private String FILES_DIRECTORY = "src/test/resources/files";
    private String fileTestName = "1.pdf";
    private ClientFileService service = new ClientFileService(DOWNLOAD_DIRECTORY);

    @Test
    public void testReceiveFile() throws IOException {
        //Given
        DataInputStream dis = new DataInputStream(new FileInputStream(FILES_DIRECTORY + File.separator + fileTestName));

        //When
        service.receiveFile(dis, 100);

        //Verify received file zip
        File[] receivedFiles = new File(DOWNLOAD_DIRECTORY).listFiles();
        List<String> fileNames = Arrays.stream(receivedFiles).map(File::getName).collect(Collectors.toList());

        Assertions.assertTrue(fileNames.stream().anyMatch(fName -> fName.contains(".zip")));

        //Clean
        Arrays.stream(receivedFiles).forEach(File::delete);

    }

    @Test
    public void testUnzip() throws IOException {
        //given
        File rawFile = new File(FILES_DIRECTORY + File.separator + fileTestName);
        String zipFileName = "1.zip";
        Files.copy(Paths.get(FILES_DIRECTORY + File.separator + "1.zip"), Paths.get(DOWNLOAD_DIRECTORY + File.separator + zipFileName));

        //when
        service.unzip(zipFileName);

        //verify
        File unzipFile  = new File(DOWNLOAD_DIRECTORY + File.separator + fileTestName);
        Assertions.assertEquals(rawFile.getName(), unzipFile.getName());
        Assertions.assertEquals(rawFile.getTotalSpace(), unzipFile.getTotalSpace());

        //clean
        Files.deleteIfExists(unzipFile.toPath());
    }


}
