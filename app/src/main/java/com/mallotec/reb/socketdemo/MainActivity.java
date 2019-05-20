package com.mallotec.reb.socketdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mallotec.reb.socketdemo.tool.ClientConnector;
import com.mallotec.reb.socketdemo.tool.StringUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ClientConnector.ConnectListener {

    private boolean isConnected = false;
    private TextView tvConnectStatus;
    private TextInputEditText etNumber;
    private TextInputEditText etName;
    private TextInputEditText etAddress;
    private Button btSend;
    private Button btConnect;

    private ClientConnector mConnector;
    private int dstPort = 2324;
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        mHandlerThread = new HandlerThread("MainActivity", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(MainActivity.this, "连接已断开请重新连接", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "已发送", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    private void initView() {
        tvConnectStatus = findViewById(R.id.tv_status_connect);
        tvConnectStatus.setVisibility(View.GONE);
        etNumber = findViewById(R.id.et_number);
        etName = findViewById(R.id.et_name);
        etAddress = findViewById(R.id.et_address);
        btSend = findViewById(R.id.bt_send);
        btConnect = findViewById(R.id.bt_connect);

        etAddress.setText("192.168.2.103");
        etNumber.setText("201611010991");
        etName.setText("Reborn");

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put("Number", Objects.requireNonNull(etNumber.getText()).toString());
                map.put("Name", Objects.requireNonNull(etName.getText()).toString());
                sendMessage(StringUtil.mapToJson(map));
            }
        });

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnector = new ClientConnector(Objects.requireNonNull(etAddress.getText()).toString(), dstPort);
                mConnector.setOnConnectListener(MainActivity.this);
                if (!isConnected) {
                    connectServer();
                    btConnect.setText("Disconnect");
                    Toast.makeText(MainActivity.this, "已连接", Toast.LENGTH_SHORT).show();
                } else {
                    disconnectServer();
                    btConnect.setText("Connect");
                    Toast.makeText(MainActivity.this, "已断开", Toast.LENGTH_SHORT).show();
                }
                isConnected = !isConnected;
            }
        });
    }
    public void connectServer() {
        new Thread(new ConnectRunnable()).start();
    }

    public void authClient() {

        String auth = Constants.ID;
        if (TextUtils.isEmpty(auth)) {
            Toast.makeText(this, "认证信息不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mConnector.auth(auth);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean changeStatus(boolean isConnected) {
        if (isConnected) {
            tvConnectStatus.setText("Connected");
            tvConnectStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            tvConnectStatus.setText("Not Connected");
            tvConnectStatus.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        this.isConnected = isConnected;
        return isConnected;
    }

    private class ConnectRunnable implements Runnable {
        @Override
        public void run() {
            try {
                mConnector.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendMessageRunnable implements Runnable {
        private String msg;

        public SendMessageRunnable(String msg){
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                Log.d("MainActivity", msg);
                mConnector.send(Constants.ID, msg);
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = -1;
                mHandler.sendMessage(message);
            }
        }
    }

    public void sendMessage(String msg) {
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(this, "信息不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new SendMessageRunnable(msg)).start();
    }

    public void disconnectServer() {
        try {
            mConnector.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onReceiveData(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("MainActivity", "服务端传来消息：" + data);
                Toast.makeText(MainActivity.this, "服务端传来消息：" + data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void callBack() {
        authClient();
    }
}
