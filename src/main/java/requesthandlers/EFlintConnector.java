package requesthandlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import response.StandardResponse;
import response.StatusResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class EFlintConnector {
    public static StandardResponse communicate(String input, int port) {
        try (Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("sent to eflint : " + input + " on port:  " + port);
            String response_str;
            writer.println(input);
            writer.flush();
            response_str = reader.readLine();
            System.out.println("got response: " + response_str);

            Gson gson = new Gson();
            if (response_str != null) {

                JsonElement responseObj = new JsonObject();
                responseObj.getAsJsonObject().add("request", gson.fromJson(input,JsonElement.class));
                responseObj.getAsJsonObject().add("response", gson.fromJson(response_str,JsonElement.class));

                System.out.println("response: "  + gson.toJson( responseObj));

                StandardResponse std_response = new StandardResponse(StatusResponse.SUCCESS, gson.toJsonTree(responseObj));
                return std_response;
//                    if (response.get("response").equals("success")) {
//                        //TODO report any violations
//                    }
//                    else if(response.get("response").equals("invalid input")) {
//                        //throw new CompilationError();
//                        System.out.println(response.get("error"));
//                    }
//                    return response;
            } else {
                System.out.println("communication error");
            }

        } catch (IOException e) {
            System.out.println("cannot communicate with localhost on port " + port);
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
