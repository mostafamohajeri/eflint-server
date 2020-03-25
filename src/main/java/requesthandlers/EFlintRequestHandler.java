package requesthandlers;

import com.google.gson.Gson;
import requests.EFlintRequest;
import response.StandardResponse;

public class EFlintRequestHandler  {


    public static StandardResponse handleRequest(EFlintRequest c,int port)
    {
        StandardResponse response = EFlintConnector.communicate(new Gson().toJson(c.getData()),port);
        return response;
    }


}
