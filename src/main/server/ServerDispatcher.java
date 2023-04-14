package main.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class ServerDispatcher {
    private static Logger log = Logger.getLogger(ServerDispatcher.class.getName());
    private static ExecutorService threadPool = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws InterruptedException, IOException {
        loggingConfig();
        ServerSocket serverSocket = new ServerSocket(5056);

        while (true) {
            Socket socket = null;

            try {
                // socket object to receive incoming main.client requests
                socket = serverSocket.accept();
                log.info("A new main.client is connected : " + socket);
                log.info("Assigning new thread for this main.client");
                threadPool.submit(new SocketHandler(socket));


            } catch (Exception e) {
                socket.close();
                threadPool.shutdown();
                threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                e.printStackTrace();
            }
        }
    }

    private static void loggingConfig() {
        Formatter myFormatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                return new StringBuffer("Thread")
                        .append("-")
                        .append(record.getThreadID())
                        .append("-")
                        .append(new Date(record.getMillis()))
                        .append("-")
                        .append(record.getMessage() + "\n")
                        .toString();
            }
        };

        Logger rootLogger = LogManager.getLogManager().getLogger("");
        for (Handler h : rootLogger.getHandlers()) {
            h.setFormatter(myFormatter);
        }
    }

}