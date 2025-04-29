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
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Client {
    private final String host;
    private final int port;
    private SocketChannel sc;
    private final String id;
    private final Log log = new Log();

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        log.add("=== " + id + " log start ===");

        int attempts = 10;

        while (attempts-- > 0) {
            try {
                sc = SocketChannel.open();
                sc.configureBlocking(false);
                sc.connect(new InetSocketAddress(host, port));

                int count = 0; // to make sure that the while loop stops
                while (!sc.finishConnect() && count < 50) { // 5 seconds in that case
                    Thread.sleep(100);
                    count++;
                }

                return;
            } catch (IOException | InterruptedException e) {
                if (attempts == 0) {
                    throw new RuntimeException(e);
                }
                try { // If server not yet ready
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public String send(String req) {
        try {
            // Writing
            ByteBuffer buf = ByteBuffer.wrap((req + "\n").getBytes(StandardCharsets.UTF_8));

            while (buf.hasRemaining()) {
                sc.write(buf);
            }

            // Reading
            Selector selector = Selector.open();
            sc.register(selector, SelectionKey.OP_READ);

            selector.select();

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iter = keys.iterator();

            StringBuilder sb = new StringBuilder();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                if (key.isReadable()) {
                    SocketChannel serverChannel = (SocketChannel) key.channel();
                    sb.append(BufferOperations.readMessage(serverChannel));
                }
            }
            String resp = sb.toString();

            // Logging
            String[] tokens = req.split(" ");
            boolean isDateRequest = !tokens[0].equals("login") && !tokens[0].equals("bye");

            if (isDateRequest) {
                log.add("Request: " + req);
                log.add("Result:\n" + resp);
            } else {
                log.add(resp);
            }


            if (req.equals("bye and log transfer")) {
                log.add("=== " + id + " log end ===");
                return log.getLog();
            }
            return isDateRequest ? resp : "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getId() {
        return id;
    }
}
