import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args)  {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ServerSocketChannel serverSocketChannel = null;

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(5001));

            System.out.println("[서버 시작]");

            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                InetSocketAddress inetSocketAddress = (InetSocketAddress) socketChannel.getRemoteAddress();

                System.out.println(inetSocketAddress.getHostName() + " 연결 수락");

                ByteBuffer byteBuffer = null;
                Charset charset = Charset.forName("UTF-8");

                byteBuffer = ByteBuffer.allocate(100);
                int byteNum = socketChannel.read(byteBuffer);
                if (byteNum == -1) {
                    throw new IOException();
                }

                byteBuffer.flip();
                String message = charset.decode(byteBuffer).toString();
                System.out.println(inetSocketAddress.getHostName() + " 데이터 받기 : " + message);

                byteBuffer = charset.encode("Hello World");
                socketChannel.write(byteBuffer);
                System.out.println(inetSocketAddress.getHostName() + " 데이터 보냄");
                System.out.println(inetSocketAddress.getHostName() + " 연결 끊기");
                socketChannel.close();
            }

        } catch (Exception e) {
        } finally {
            try {
                serverSocketChannel.close();
            } catch (IOException e) {}
        }

    }
}
