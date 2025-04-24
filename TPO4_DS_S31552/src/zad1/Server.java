/**
 *
 *  @author Dyrda Stanisław S31552
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server implements Runnable {
    private final String host;
    private final int port;
    private ServerSocketChannel ssc;
    private Selector selector;
    private final Log log = new Log(this);
    private volatile boolean isRunning = false;

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

    private void serviceRequest(SocketChannel sc) {
        if (!sc.isOpen()) return;

        ByteBuffer bbuf = ByteBuffer.allocate(1024);
    }
}
