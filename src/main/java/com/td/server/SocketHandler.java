package com.td.server;


import com.td.common.CommandType;
import com.td.server.command.GetCommand;
import com.td.server.command.IndexCommand;
import com.td.server.command.QuitCommand;
import com.td.server.configuration.FileConfiguration;
import com.td.server.services.ServerFileService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.td.server.configuration.FileConfiguration.FILE_SYSTEM_DIRECTORY;


class SocketHandler implements Runnable {

    private static Logger log = Logger.getLogger(SocketHandler.class.getName());
    private DataInputStream dis;
    private DataOutputStream dos;
    final Socket socket;

    public SocketHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        String receivedCommand;

        try {
            // Ask user what main.client wants
            dos.writeUTF("Type the command\n" +
                    "        - index: To list out all the available files\n" +
                    "        - get <file-name>: To download the file-name file \n" +
                    "        - quit/q: to exit the main.server");
            outerloop:
            while (true) {
                receivedCommand = dis.readUTF();
                ServerFileService serverFileService = new ServerFileService(FILE_SYSTEM_DIRECTORY);
                switch (CommandType.getCommand(receivedCommand)) {
                    case QUIT:
                    case Q:
                        new QuitCommand(socket).process();
                        break outerloop;

                    case INDEX:
                        new IndexCommand(dos, serverFileService).process();
                        break;

                    case GET:
                        try {
                            String fileNames = receivedCommand.substring(receivedCommand.indexOf(' '), receivedCommand.length());

                            new GetCommand(dos, serverFileService, fileNames).process();
                            dis.readInt();
                            break;
                        } catch (FileNotFoundException e) {
                            try {
                                String msg = e.getMessage();
                                log.log(Level.SEVERE, msg);
                                dos.writeBoolean(Boolean.FALSE);
                                dos.writeUTF(msg);
                            } catch (IOException ex) {
                                closeResources();
                            }
                        }
                }
            }
        } catch (IllegalArgumentException e) {
            log.warning("Unknown command");
        } catch (SocketException e) {
            log.info("Client closed connection !");
        } catch (IOException e) {
            e.printStackTrace();
            closeResources();
        }
    }

    private void closeResources() {
        try {
            this.socket.close();
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            log.log(Level.SEVERE, "Error when closing resource!");
            e.printStackTrace();
        }
    }

}
