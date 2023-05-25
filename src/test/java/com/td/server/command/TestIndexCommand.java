package com.td.server.command;

import com.td.server.services.ServerFileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class TestIndexCommand {
    private ServerFileService serverFileService = mock(ServerFileService.class);
    private DataOutputStream dos = mock(DataOutputStream.class, Mockito.RETURNS_DEEP_STUBS);
    private String FILES_DIRECTORY = "src/test/resources/files";
    private String fileTestName = "1.pdf";

    @Test
    public void testProcess() throws IOException {
        //Given
        File file = new File(FILES_DIRECTORY + File.separator + fileTestName);
        File[] files = new File[]{file};
        when(serverFileService.listFiles()).thenReturn(files);
        when(serverFileService.formatFilesToString(files)).thenReturn("StringToClient");
        doNothing().when(dos).writeUTF("StringToClient");
        IndexCommand indexCommand = new IndexCommand(dos, serverFileService);

        //when
        indexCommand.process();

        verify(serverFileService).listFiles();
        verify(serverFileService).formatFilesToString(files);
        verify(dos).writeUTF("StringToClient");
    }
}
