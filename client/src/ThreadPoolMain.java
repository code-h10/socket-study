import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ThreadPoolMain {
    public ThreadPoolMain() {
    }

    public static void main(String[] args) {

        for (int i = 0; i <= 100; i++) {

            SocketChannel socketChannel = null;

            try {
                socketChannel = SocketChannel.open();
                System.out.println("[연결 요청]");
                socketChannel.connect(new InetSocketAddress("localhost", 5001));
                System.out.println("[연결 성공]");

                ByteBuffer byteBuffer = null;
                Charset charset = Charset.forName("UTF-8");

                byteBuffer = charset.encode("Hello World" + i);
                socketChannel.write(byteBuffer);
                System.out.println(i + "번째 데이터 보냄");

                byteBuffer = ByteBuffer.allocate(100);
                int byteNumber = socketChannel.read(byteBuffer);
                if (byteNumber == -1) {
                    throw new IOException();
                }

                byteBuffer.flip();
                String message = charset.decode(byteBuffer).toString();
                System.out.println(i + "번째 데이터 받기" + message);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    System.out.println("[연결 끊기]");
                    socketChannel.close();
                } catch (IOException e) {
                }
            }
            System.out.println();
        }
    }
}
