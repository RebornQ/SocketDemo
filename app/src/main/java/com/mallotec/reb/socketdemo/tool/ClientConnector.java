package com.mallotec.reb.socketdemo.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientConnector {

    private Socket mClient;
    /**
     * 服务端的ip
     */
    private String mDstName;
    /**
     * 服务端端口号
     */
    private int mDesPort;

    private ConnectListener mListener;


    public ClientConnector(String dstName, int dstPort) {
        this.mDstName = dstName;
        this.mDesPort = dstPort;
    }

    /**
     * 与服务端进行连接
     *
     * @throws IOException
     */
    public void connect() throws IOException {
        if (mClient == null) {
            mClient = new Socket(mDstName, mDesPort);
        }
        if (mListener != null) {
            mListener.callBack();
        }
        //获取其他客户端发送过来的数据
        InputStream inputStream = mClient.getInputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(buffer)) != -1) {
            String data = new String(buffer, 0, len, StandardCharsets.UTF_8);

            //通过回调接口将获取到的数据推送出去
            if (mListener != null) {
                mListener.onReceiveData(data);
            }
        }
    }

    /**
     * 认证方法，这个方法是用来进行客户端一对一发送消息的
     * 在实际项目中进行即时通讯时都需要进行登录，这里就是
     * 模拟客户端的账号
     *
     * @param authName
     */
    public void auth(String authName) throws IOException {
        if (mClient != null) {
            //将客户端账号发送给服务端，让服务端保存
            OutputStream outputStream = mClient.getOutputStream();
            //模拟认证格式，以#开头
            outputStream.write(("#" + authName).getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * 发送数据
     *
     * @param data 需要发送的内容
     */
    public void send(String data) throws IOException {
        OutputStream outputStream = mClient.getOutputStream();
        //模拟内容格式：content
        outputStream.write((data).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将数据发送给指定的接收者
     *
     * @param receiver 信息接数者
     * @param data  需要发送的内容
     */
    public void send(String receiver, String data) throws IOException {
        OutputStream outputStream = mClient.getOutputStream();
        //模拟内容格式：receiver+  # + content
        outputStream.write((receiver + "#" + data).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 断开连接
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {
        if (mClient != null) {
            mClient.shutdownInput();
            mClient.shutdownOutput();
            mClient.close();
            mClient = null;
        }
    }


    public void setOnConnectListener(ConnectListener listener) {
        this.mListener = listener;
    }

    /**
     * 数据接收回调接口
     */
    public interface ConnectListener {
        void onReceiveData(String data);

        void callBack();
    }
}