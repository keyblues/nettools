package network;

import java.io.*;
import java.net.*;

public class Whois {

    public String query(String domain, String whoisServer) {
        StringBuilder result = new StringBuilder();
        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            int port = 43;
            socket = new Socket(whoisServer, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println(domain);

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (IOException e) {
            result.append("Error executing Whois query: ").append(e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                result.append("Error closing resources: ").append(e.getMessage());
            }
        }
        return result.toString();
    }
}
