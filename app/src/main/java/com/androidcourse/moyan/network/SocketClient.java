package com.androidcourse.moyan.network;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SocketClient {
    private static final String SERVER_URL = "ws://10.0.2.2:8888/ws";
    private static final int TIMEOUT = 30;

    private static SocketClient instance;
    private MyWebSocketClient client;

    private SocketClient() {}

    public static synchronized SocketClient getInstance() {
        if (instance == null) {
            instance = new SocketClient();
        }
        return instance;
    }

    public synchronized String sendRequest(String jsonRequest) {
        final CountDownLatch latch = new CountDownLatch(1);
        final StringBuilder responseBuilder = new StringBuilder();

        try {
            Log.d("SocketClient", "发送请求: " + jsonRequest);

            URI uri = URI.create(SERVER_URL);
            client = new MyWebSocketClient(uri) {
                @Override
                public void onMessage(String message) {
                    Log.d("SocketClient", "收到响应: " + message);
                    responseBuilder.append(message);
                    latch.countDown();
                }

                @Override
                public void onError(Exception ex) {
                    Log.e("SocketClient", "WebSocket错误: " + ex.getMessage());
                    ex.printStackTrace();
                    latch.countDown();
                }
            };

            if (client.connectBlocking(TIMEOUT, TimeUnit.SECONDS)) {
                Log.d("SocketClient", "WebSocket连接成功");
                client.send(jsonRequest);

                if (latch.await(TIMEOUT, TimeUnit.SECONDS)) {
                    return responseBuilder.toString();
                } else {
                    return "{\"code\":1,\"msg\":\"请求超时\",\"data\":null}";
                }
            } else {
                return "{\"code\":1,\"msg\":\"连接失败\",\"data\":null}";
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SocketClient", "网络请求失败: " + e.getMessage());
            return "{\"code\":1,\"msg\":\"网络连接失败：" + e.getMessage() + "\",\"data\":null}";
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    private abstract static class MyWebSocketClient extends WebSocketClient {
        public MyWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d("SocketClient", "WebSocket已打开");
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.d("SocketClient", "WebSocket已关闭: " + reason);
        }
    }
}
