import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class AsynchronousClientSocket {

    public static void main(String[] args) {

        System.out.println("[클라이언트 시작]");

        try {
            for (int i = 1; i <= 100; i++) {
                AsynchronousSocketChannel asc = AsynchronousSocketChannel.open();

                int count = i;

                asc.connect(new InetSocketAddress("localhost", 5001), null, new CompletionHandler<Void, Void>() {
                    @Override
                    public void completed(Void result, Void attachment) {
                        send(asc, count);
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        exc.printStackTrace();
                        try {
                            asc.close();
                        } catch (Exception e) {}
                    }
                });
            }

            try {
                System.in.read();
            } catch (Exception e) {}

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[클라이언트 종료]");
    }

    public static void send(AsynchronousSocketChannel asc, int count) {
        Charset charset = Charset.forName ("utf-8");
        String sendData = "Hello Server " + count;
        ByteBuffer byteBuffer = charset.encode(sendData);

        asc.write(byteBuffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) { System.out.println("데이터 보냄: " + sendData);

                //서버가 보낸 데이터 받기
                receive(asc);
            }
            @Override
            public void failed(Throwable exc, Void attachment) { exc.printStackTrace();
                try {
                    asc.close();
                } catch (Exception e) { }
            } });

    }

    // 데이터 받기
    public static void receive(AsynchronousSocketChannel asc) {
        ByteBuffer byteBuffer = ByteBuffer.allocate (100);
        asc.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {

            @Override
            public void completed(Integer result, ByteBuffer attachment) { try {
                attachment.flip();
                Charset charset = Charset.forName ("utf-8");
                String receiveData = charset.decode(attachment).toString(); System.out.println("데이터 받음: " + receiveData);
                asc.close();
            } catch (Exception e) {
            } }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) { exc.printStackTrace();
                try { asc.close(); } catch (Exception e) {}
            } });
    }
}
