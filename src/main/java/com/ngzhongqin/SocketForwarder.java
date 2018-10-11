package com.ngzhongqin;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import java.net.*;
import java.io.*;
import javax.net.ssl.*;

public class SocketForwarder {
    public final static Logger logger = Logger.getLogger(SocketForwarder.class);

    public String tunnelHost="localhost";
    public int tunnelPort = 8080;
    public String targetHost="www.google.com";
    public int targetPort = 443;

    public void forwarding() {

        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            Socket tunnel = new Socket(tunnelHost, tunnelPort);
            doTunnelHandshake(tunnel, targetHost, targetPort);

            SSLSocket socket = (SSLSocket) factory.createSocket(tunnel, targetHost, targetPort, true);

            socket.addHandshakeCompletedListener(
                    new HandshakeCompletedListener() {
                        public void handshakeCompleted(
                                HandshakeCompletedEvent event) {
                            System.out.println("Handshake finished!");
                            System.out.println(
                                    "\t CipherSuite:" + event.getCipherSuite());
                            System.out.println(
                                    "\t SessionId " + event.getSession());
                            System.out.println(
                                    "\t PeerHost " + event.getSession().getPeerHost());
                        }
                    }
            );

            socket.startHandshake();

            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())));

            out.println("GET / HTTP/1.0");
            out.println();
            out.flush();

            if (out.checkError())
                System.out.println(
                        "SSLSocketClient:  java.io.PrintWriter error");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);

            in.close();
            out.close();
            socket.close();
            tunnel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doTunnelHandshake(Socket tunnel, String host, int port)
            throws IOException {
        OutputStream out = tunnel.getOutputStream();
        String msg = "CONNECT " + host + ":" + port + " HTTP/1.0\n"
                + "User-Agent: "
                + sun.net.www.protocol.http.HttpURLConnection.userAgent
                + "\r\n\r\n";
        byte b[];
        try {
            /*
             * We really do want ASCII7 -- the http protocol doesn't change
             * with locale.
             */
            b = msg.getBytes("ASCII7");
        } catch (UnsupportedEncodingException ignored) {
            /*
             * If ASCII7 isn't there, something serious is wrong, but
             * Paranoia Is Good (tm)
             */
            b = msg.getBytes();
        }
        out.write(b);
        out.flush();


        byte reply[] = new byte[200];
        int replyLen = 0;
        int newlinesSeen = 0;
        boolean headerDone = false;     /* Done on first newline */

        InputStream in = tunnel.getInputStream();
        boolean error = false;

        while (newlinesSeen < 2) {
            int i = in.read();
            if (i < 0) {
                throw new IOException("Unexpected EOF from proxy");
            }
            if (i == '\n') {
                headerDone = true;
                ++newlinesSeen;
            } else if (i != '\r') {
                newlinesSeen = 0;
                if (!headerDone && replyLen < reply.length) {
                    reply[replyLen++] = (byte) i;
                }
            }
        }

        String replyStr;
        try {
            replyStr = new String(reply, 0, replyLen, "ASCII7");
        } catch (UnsupportedEncodingException ignored) {
            replyStr = new String(reply, 0, replyLen);
        }

        if (!replyStr.startsWith("HTTP/1.0 200")) {
            throw new IOException("Unable to tunnel through "
                    + tunnelHost + ":" + tunnelPort
                    + ".  Proxy returns \"" + replyStr + "\"");
        }

        /* tunneling Handshake was successful! */
    }

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        logger.info("Started");
        new SocketForwarder().forwarding();
    }

}



