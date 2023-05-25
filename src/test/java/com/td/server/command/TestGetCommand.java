package com.td.server.command;

import com.td.server.services.ServerFileService;
import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestGetCommand {
    private ServerFileService serverFileService = mock(ServerFileService.class);
    private OutputStream out = mock(OutputStream.class);
    private DataOutputStream dos = new DataOutputStream (out);

    private String fileNames = "test";
    private String tempFilePath = "tempFilePath";
    @Test
    public void testProcess() throws IOException {
        //Given
        when(serverFileService.getTempFilePath()).thenReturn(tempFilePath);
        when(serverFileService.getFileSize(tempFilePath)).thenReturn(100L);
        when(serverFileService.zipFiles(List.of(fileNames),tempFilePath)).thenReturn(tempFilePath);
        doNothing().when(serverFileService).sendFiles(any(), any());

        doNothing().when(out).write(any());
        GetCommand getCommand = new GetCommand(dos,serverFileService,fileNames);

        //when
        getCommand.process();

        //verify
        verify(serverFileService).zipFiles(any(), any());
        verify(serverFileService).sendFiles(any(), any());
    }
}
