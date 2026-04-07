package com.androidcourse.moyan.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    // TODO: 改成你电脑的实际IP地址
    private static final String SERVER_IP = "192.168.***.***";  // 必须修改
    private static final int SERVER_PORT = 8888;

    private static SocketClient instance;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

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
    public String sendRequest(String jsonRequest) {
        String response = null;
        try {
            // 建立连接
            socket = new Socket(SERVER_IP, SERVER_PORT);
            socket.setSoTimeout(10000); // 10秒超时

            // 发送请求
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            output.print(jsonRequest + "\n\n");  // 服务端要求末尾加两个换行符
            output.flush();

            // 接收响应
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                sb.append(line);
            }
            response = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            response = "{\"code\":1,\"msg\":\"网络连接失败：" + e.getMessage() + "\",\"data\":null}";
        } finally {
            close();
        }

        return response;
    }

    private void close() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}