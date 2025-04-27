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
    private final String id;
    private SocketChannel sc;

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        try {
            sc = SocketChannel.open();
            sc.configureBlocking(false);
            sc.connect(new InetSocketAddress(host, port)); // this makes key.isAcceptable = true
            while (!sc.finishConnect()) {
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String send(String req) {
        try {
            // Writing
            req += "\n";

            ByteBuffer buf = ByteBuffer.wrap(req.getBytes(StandardCharsets.UTF_8));

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

            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
