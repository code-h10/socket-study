import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolMain {

    public static void main(String[] args) {

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(10);

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(5001));
            System.out.println("[서버 시작]");

            executorService.execute(() ->{

                try {

                    while (true) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        System.out.println();
                        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
                        System.out.println(inetSocketAddress.getHostName() + " 연결 수락");

                        executorService.execute(() ->{

                            String threadName = Thread.currentThread().getName();

                            try {
                                Charset charset = Charset.forName("UTF-8");

                                ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                                int byteNumber = socketChannel.read(byteBuffer);
                                if (byteNumber == -1) {
                                    throw new IOException();
                                }

                                byteBuffer.flip();
                                String message = charset.decode(byteBuffer).toString();
                                System.out.println("[" + threadName + "]" + inetSocketAddress.getHostName() + "데이터 받기" + message);

                                byteBuffer = charset.encode("Hello World");
                                socketChannel.write(byteBuffer);
                                System.out.println("[" + threadName + "]" + inetSocketAddress.getHostName() + " 데이터 보냄");


                            } catch (Exception e) {
                            } finally {
                                try {
                                    System.out.println("[" + threadName + "]" + inetSocketAddress.getHostName() + " 연결 끊기");
                                    socketChannel.close();
                                } catch (Exception e) {
                                }
                            }
                        });

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        serverSocketChannel.close();
                        executorService.shutdown();
                    } catch (Exception e) {
                    }
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
