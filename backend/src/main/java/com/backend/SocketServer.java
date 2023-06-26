package com.backend;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Objects;

public class SocketServer {
    public static void sendData(String host, String str) {
        try {
            Socket socket = new Socket(host, 8888);

            OutputStream os = socket.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

            bw.write(str);
            bw.flush();
            socket.shutdownOutput();

            while (true) {
                DataInputStream input = new DataInputStream(socket.getInputStream());
                byte[] buffer;
                buffer = new byte[input.available()];
                if (buffer.length != 0) {
                    System.out.println("length=" + buffer.length);
                    input.read(buffer);
                    String three = new String(buffer);
                    System.out.println(three);
                    if (Objects.equals(three, "5")) {
                        break;
                    }
                }
            }

            socket.shutdownInput();


            bw.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}