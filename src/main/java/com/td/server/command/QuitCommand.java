package com.td.server.command;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class QuitCommand implements Command {
    private static Logger logger = Logger.getLogger(QuitCommand.class.getName());
    private Socket socket;

    public QuitCommand(Socket socket) {
        this.socket = socket;

    }

    public void process() throws IOException {
        logger.info("Client " + this.socket + " sends exit");
        logger.info("Closing this connection");
        socket.close();
        logger.info("Socket connection closed");
        logger.info("Executing quit command");
    }
}

