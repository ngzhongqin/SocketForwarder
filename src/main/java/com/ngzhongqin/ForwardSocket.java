package com.ngzhongqin;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class ForwardSocket {
    public final static Logger logger = Logger.getLogger(ForwardSocket.class);
    public final static String NEW_LINE = "\n\r\n";

    public ForwardSocket(){
    }

    public String forwardRequest(String hostname, int port, InputStream inputStream) {
        try {
            Socket s = new Socket(hostname,port);
            PrintWriter pw = new PrintWriter(s.getOutputStream());

            String httpRequest = "";
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            String line = reader.readLine();
            while (!line.isEmpty()) {
                logger.info("line:"+line);
                if(line.contains("Host")){
                    line="Host: "+hostname;
                }
//                pw.print((line+NEW_LINE));

//                pw.print((line+NEW_LINE).getBytes("UTF-8"));
                httpRequest = httpRequest + "\n" + line;
                line = reader.readLine();
            }
            logger.info("httpRequest:"+httpRequest);

            pw.print("GET  HTTP/1.1"+NEW_LINE);
            pw.print("Host: google.com"+NEW_LINE);
            pw.print("Cache-Control: no-cache"+NEW_LINE);
//            pw.print(httpRequest.getBytes("UTF-8"));
            pw.println("");
            pw.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String httpResponse = "";
            String t;
            while((t = br.readLine()) != null){
                httpResponse=httpResponse+t;
                if(!t.isEmpty()){
                    httpResponse=httpResponse+NEW_LINE;
                }
            }
            br.close();
            logger.info("httpResponse:"+httpResponse);

            logger.info("End of Forward Socket");
            return httpResponse;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
