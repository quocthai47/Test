package com.td.server.command;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.mockito.Mockito.*;

public class TestQuitCommand {
    Socket socket = mock(Socket.class);
    @Test
    public void testProcess() throws IOException {
        //Given
        doNothing().when(socket).close();
        QuitCommand quitCommand = new QuitCommand(socket);

        //when
        quitCommand.process();

        //Verify
        verify(socket).close();

    }
}
