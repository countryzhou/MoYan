package com.androidcourse.moyan.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Socket 网络客户端 - 单例
 * 负责与服务器建立TCP连接、发送JSON请求、接收响应
 */
public class SocketClient {
    // TODO: 改成你电脑的实际IP地址
    private static final String SERVER_IP = "10.0.2.2";
    private static final int SERVER_PORT = 8888;

    private static SocketClient instance;

    private SocketClient() {}

    public static SocketClient getInstance() {
        if (instance == null) {
            instance = new SocketClient();
        }
        return instance;
    }

    /**
     * 发送JSON请求并返回响应
     * @param jsonRequest 符合服务端格式的JSON字符串
     * @return 服务端返回的JSON字符串
     */
    public synchronized String sendRequest(String jsonRequest) {
        Socket socket = null;
        PrintWriter output = null;
        BufferedReader input = null;

        try {
            Log.d("SocketClient", "发送请求: " + jsonRequest);

            socket = new Socket(SERVER_IP, SERVER_PORT);
            socket.setSoTimeout(30000);

            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            output.print(jsonRequest + "\n\n");
            output.flush();

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(line);
            }

            String response = sb.toString();
            Log.d("SocketClient", "收到响应: " + response);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SocketClient", "网络请求失败: " + e.getMessage());
            return "{\"code\":1,\"msg\":\"网络连接失败：" + e.getMessage() + "\",\"data\":null}";
        } finally {
            // 关闭资源
            try {
                if (input != null) input.close();
                if (output != null) output.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
