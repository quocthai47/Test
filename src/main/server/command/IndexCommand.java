package main.server.command;

import main.server.ServerFileService;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class IndexCommand implements Command {
    private ServerFileService serverFileService;
    private DataOutputStream dos;
    public IndexCommand(DataOutputStream dos, ServerFileService serverFileService) {
        this.serverFileService = serverFileService;
        this.dos = dos;
    }

    @Override
    public void process() throws IOException {
        File file = new File("files");
        File[] files = file.listFiles();
        StringBuffer sb = serverFileService.listFiles(files);
        dos.writeUTF(sb.toString());
    }
}
