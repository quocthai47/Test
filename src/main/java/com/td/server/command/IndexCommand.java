package com.td.server.command;


import com.td.server.services.ServerFileService;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class IndexCommand implements Command {
    private ServerFileService fileService;
    private DataOutputStream dos;


    public IndexCommand(DataOutputStream dos, ServerFileService serverFileService) {
        this.fileService = serverFileService;
        this.dos = dos;
    }

    @Override
    public void process() throws IOException {
        File[] files = fileService.listFiles();
        dos.writeUTF(fileService.formatFilesToString(files));
    }
}
