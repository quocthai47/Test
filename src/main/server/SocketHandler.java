package main.server;

import main.common.CommandType;
import main.server.command.GetCommand;
import main.server.command.IndexCommand;
import main.server.command.QuitCommand;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;


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
                ServerFileService serverFileService = new ServerFileService();
                switch (CommandType.getCommand(receivedCommand)) {
                    case QUIT:
                    case Q:
                        new QuitCommand().process();
                        log.info("client " + this.socket + " sends exit");
                        log.info("Closing this connection");
                        this.socket.close();
                        log.info(" Socket connection closed");
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
