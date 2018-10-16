package com.ngzhongqin;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class ForwardSocket {
    public final static Logger logger = Logger.getLogger(ForwardSocket.class);

    public ForwardSocket(){
    }

    public InputStream forwardRequest(String hostname, int port, InputStream inputStream) {
        try {
            String httpRequest = "";
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            String line = reader.readLine();
            while (!line.isEmpty()) {
//                logger.info(line);
                httpRequest = httpRequest + "\n" + line;
                line = reader.readLine();
            }
            logger.info("httpRequest:"+httpRequest);


            Socket s = new Socket(hostname,port);
            PrintWriter pw = new PrintWriter(s.getOutputStream());
            pw.print(httpRequest.getBytes("UTF-8"));
            pw.println("");
            pw.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String httpResponse = "";
            String t;
            while((t = br.readLine()) != null){
                httpResponse=httpResponse+t;
            }
            br.close();
            logger.info("httpResponse:"+httpResponse);

            logger.info("End of Forward Socket");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
