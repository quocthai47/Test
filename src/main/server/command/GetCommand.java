package main.server.command;

import main.server.ServerFileService;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class GetCommand implements Command {
    private DataOutputStream dos;
    private ServerFileService serverFileService;
    private String fileNames;
    private String tempFilePath;

    public GetCommand(DataOutputStream dos, ServerFileService serverFileService, String fileNames) {
        this.dos = dos;
        this.serverFileService = serverFileService;
        this.fileNames = fileNames;
        this.tempFilePath = serverFileService.getTempFilePath();
    }

    public void process() throws IOException {
        String[] splitFileNames = fileNames.trim().split("\\s+");
        try {
            String zipFilePath = serverFileService.zipFiles(Arrays.asList(splitFileNames), tempFilePath);
            sendAckFoundFile();
            sendFileSize();
            serverFileService.sendFiles(dos, zipFilePath);
        } finally {
            Files.deleteIfExists(Paths.get(tempFilePath));
        }
    }

    private void sendAckFoundFile() throws IOException {
        dos.writeBoolean(Boolean.TRUE);
    }

    private void sendFileSize() throws IOException {
        dos.writeLong(Files.size(Paths.get(tempFilePath)));
    }

}
