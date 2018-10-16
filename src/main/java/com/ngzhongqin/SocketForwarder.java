package com.ngzhongqin;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketForwarder {
    public final static Logger logger = Logger.getLogger(SocketForwarder.class);

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        ServerSocket listener = new ServerSocket(9090);
        logger.info("Started");
        try {
            while (true) {
                Socket socket = listener.accept();
                try {

                    new ForwardSocket().forwardRequest("google.com",80,socket.getInputStream());

                    String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + "hello";
                    socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));

                } finally {
                    socket.close();
                }
            }
        }
        finally {
            listener.close();
        }
    }
}
