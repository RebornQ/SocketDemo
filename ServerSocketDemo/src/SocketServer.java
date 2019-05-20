import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

public class SocketServer {

    /**
     * 用来保存不同的客户端
     */
    private static Map<String, Socket> mClients = new LinkedHashMap<>();

    public static void main(String[] args) {

        int port = 2324;
        try {
            //创建服务器
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("正在等待连接...");
            while (true) {
                //获取客户端的连接
                final Socket socket = serverSocket.accept();
                if (socket.isConnected()) {
                    System.out.println(socket.getRemoteSocketAddress() + "连接成功!");
                    System.out.println("等待接收" + socket.getRemoteSocketAddress() + "的消息...");
                }
                new Thread(() -> {
                    try {
                        //读取从客户端发送过来的数据
                        InputStream inputStream = socket.getInputStream();
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = inputStream.read(buffer)) != -1) {
                            String data = new String(buffer, 0, len);
                            System.out.println(data);

                            // 先认证客户端
                            if (data.startsWith("#")) {
                                mClients.put(data, socket);
                            } else {
                                // 将数据发送给指定的客户端
                                String[] split = data.split("#");
                                Socket c = mClients.get("#" + split[0]);
                                OutputStream outputStream = c.getOutputStream();
                                outputStream.write(split[1].getBytes());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}