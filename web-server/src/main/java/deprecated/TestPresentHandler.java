package deprecated;

import com.google.gson.Gson;
import requesthandlers.EFlintConnector;
import requesthandlers.RequestHandler;
import response.StandardResponse;

public class TestPresentHandler implements RequestHandler<ActionCommand> {


    public StandardResponse handle(ActionCommand c,int port)
    {
        StandardResponse response = EFlintConnector.communicate(new Gson().toJson(c),port);
        return response;
    }


}
