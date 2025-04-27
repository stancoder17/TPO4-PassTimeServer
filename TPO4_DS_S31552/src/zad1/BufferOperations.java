package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class BufferOperations {
    public static String readMessage(SocketChannel sc) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        StringBuilder sb = new StringBuilder();

        while (sc.read(buf) > 0) {
            buf.flip();
            while (buf.hasRemaining()) {
                sb.append((char) buf.get());
            }
            buf.clear();
        }

        return sb.toString().trim();
    }
}
