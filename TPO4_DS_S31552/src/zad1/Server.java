/**
 *
 *  @author Dyrda Stanisław S31552
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Server implements Runnable {
    private final String host;
    private final int port;
    private ServerSocketChannel ssc;
    private Selector selector;
    private final Log log = new Log(this);
    private volatile boolean isRunning = false;
    private final Map<SocketChannel, String> clientLogins = new HashMap<>();

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        System.out.println("Starting server: " + host + ":" + port);

        try {
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(new InetSocketAddress(host, port));

            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server started: " + host + ":" + port);
            isRunning = true;
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while starting the server", e);
        }
        serviceConnections();
    }

    public void startServer() {
        Thread t = new Thread(this);
        t.start();
    }

    public void stopServer() {
        try {
            System.out.println("Stopping server: " + host + ":" + port);
            isRunning = false;

            ssc.close();
            selector.close();

            System.out.println("Server stopped: " + host + ":" + port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void serviceConnections() {
        while (isRunning) {
            try {
                // Wait for incoming operations
                selector.select();

                // An operation occured
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if (key.isAcceptable()) { // Client requests a connection
                        SocketChannel clientChannel = ssc.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        continue;
                    }

                    if (key.isReadable()) { // Client sends a request
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        serviceRequest(clientChannel);
                        continue;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private void serviceRequest(SocketChannel sc) throws IOException {
        if (!sc.isOpen() || sc.socket().isClosed()) return;

        String request = BufferOperations.readMessage(sc);

        // If client not logged in
        synchronized (clientLogins) {
            if (!clientLogins.containsKey(sc)) {
                if (isValidLogin(request)) {
                    clientLogins.put(sc, request.split(" ")[1]); // login Adam -> put(sc, "Adam")
                    writeResponse(sc, "logged in");
                } else {
                    writeResponse(sc, "incorrect login, connect again");
                    sc.close();
                    sc.socket().close();
                }
                return;
            }
        }

        String[] tokens = request.split(" ");

        String command = tokens[0];
        if (command.equals("bye")) {
            writeResponse(sc, "logged out");
            clientLogins.remove(sc);
            sc.close();
            sc.socket().close();
        }
        else if (command.equals("bye and log transfer")) { // TO BE IMPLEMENTED
            writeResponse(sc, "client's log...");
            sc.close();
            sc.socket().close();
        }
        else {
            if (tokens.length != 2) {
                writeResponse(sc, "incorrect date command");
                return;
            }
            String dateFrom = tokens[0];
            String dateTo = tokens[1];

            String dateMsg = Time.passed(dateFrom, dateTo);
            writeResponse(sc, dateMsg);
        }

    }

    private void writeResponse(SocketChannel sc, String message) throws IOException {
        message += "\n";

        ByteBuffer buf = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));

        while (buf.hasRemaining())
            sc.write(buf);
    }

    private boolean isValidLogin(String msg) {
        if (msg != null) {
            String[] tokens = msg.split(" ");
            return tokens.length == 2 && tokens[0].equals("login");
        }
        return false; // else
    }
}
