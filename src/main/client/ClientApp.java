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
            String firstMsgFromServer = dis.readUTF();
            outerloop:
            while (true) {
                try {
                    System.out.println(firstMsgFromServer);
                    String tosend = scn.nextLine();
                    CommandType command = CommandType.getCommand(tosend);
                    dos.writeUTF(tosend);
                    switch (command) {
                        case QUIT:
                        case Q:
                            System.out.println("Closing this connection : " + socket);
                            socket.close();
                            System.out.println("Connection closed");
                            break outerloop;
                        case INDEX:
                            String received = dis.readUTF();
                            System.out.println(received);
                            break;
                        case GET:
                            ClientFileService fileService = new ClientFileService();
                            long fileSize = receiveAckFoundedFileAndFileSize(dis);

                            String tempZipFileName = fileService.receiveFile(dis, fileSize);

                            sendAckReceivedFile(dos);

                            fileService.unzip(tempZipFileName);
                            break;
                    }
                } catch (IllegalArgumentException ex) {
                    System.out.println("The unknown command which is not supported by server");
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            }

            // closing resources
            scn.close();
            dis.close();
            dos.close();
        } catch (Exception e) {
            System.out.println("The connection has been lost. attempting to reconnect to your connection !");
            e.printStackTrace();
        }
    }

    private static long receiveAckFoundedFileAndFileSize(DataInputStream dis) throws IOException {
        boolean isFoundFile = dis.readBoolean();
        System.out.println("isFoundFile" + isFoundFile);
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
