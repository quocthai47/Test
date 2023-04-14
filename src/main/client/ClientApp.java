package main.client;

import main.common.CommandType;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {

    public static void main(String[] args) {
        try {
            Scanner scn = new Scanner(System.in);
            InetAddress ip = InetAddress.getByName("localhost");

            Socket socket = new Socket(ip, 5056);

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            outerloop:
            while (true) {
                System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);
                try {
                    switch (CommandType.getCommand(tosend)) {
                        case QUIT:
                        case Q:
                            System.out.println("Closing this connection : " + socket);
                            socket.close();
                            System.out.println("Connection closed");
                            break outerloop;
                        case INDEX:
                            String received = dis.readUTF();
                            System.out.println(received);
                            break ;
                        case GET:
                            try {
                                ClientFileService fileService = new ClientFileService();
                                long fileSize = receiveAckFoundedFileAndFileSize(dis);

                                String tempZipFileName = fileService.receiveFile(dis, fileSize);

                                sendAckReceivedFile(dos);

                                fileService.unzip(tempZipFileName);
                                break;

                            } catch (FileNotFoundException e) {
                                System.out.println(e.getMessage());
                            }

                    }
                } catch (IllegalArgumentException ex) {
                    //Maybe the new command in main.server
                }
            }

            // closing resources
            scn.close();
            dis.close();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long receiveAckFoundedFileAndFileSize(DataInputStream dis) throws IOException {
        boolean isFoundFile = dis.readBoolean();
        if (!isFoundFile) {
            String msg = dis.readUTF();
            throw new FileNotFoundException(msg);
        }
        return dis.readLong();
    }

    private static void sendAckReceivedFile(DataOutputStream dos) throws IOException {
        dos.writeInt(0);
    }

}
