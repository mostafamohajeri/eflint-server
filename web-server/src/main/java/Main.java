import com.google.gson.Gson;
import eflint.InstanceManager;
import requesthandlers.EFlintRequestHandler;
import requests.CreateEFlintInstanceRequest;
import requests.EFlintRequest;
import response.StandardResponse;
import response.StatusResponse;

import java.util.concurrent.CompletableFuture;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        post("/command", (request, response) -> {

            response.type("" +
                    "application/json");

            EFlintRequest eFlintRequest =
                    new Gson().fromJson(request.body(), EFlintRequest.class);

            if(eFlintRequest.getUuid() == null) {
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR,"please provide a valid uuid"));
            }


            int port = InstanceManager.getInstance().getPortByUUID(eFlintRequest.getUuid());

            if(port == -1) {
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR,"no such instance present"));
            }

            return new Gson().toJson(EFlintRequestHandler.handleRequest(eFlintRequest,port));

        });

//        post("/command", (request, response) -> {
//
//            response.type("application/json");
//
//            EFlintRequest eFlintRequest =
//                    new Gson().fromJson(request.body(), EFlintRequest.class);
//
//            int port = 4242;
//
//            System.out.println(eFlintRequest);
//
//            StandardResponse r = EFlintRequestHandler.handleRequest(eFlintRequest,port);
//
//            return new Gson().toJson(r);
//        });

        post("/create", (request, response) -> {

            response.type("application/json");

            CreateEFlintInstanceRequest createEFlintInstanceRequest =
                    new Gson().fromJson(request.body(), CreateEFlintInstanceRequest.class);

            System.out.println(new Gson().toJson(createEFlintInstanceRequest));

            CompletableFuture<StandardResponse> r = InstanceManager.getInstance().createNewInstance(createEFlintInstanceRequest);

            return new Gson().toJson(r.get());

        });

        post("/kill", (request, response) -> {

            response.type("application/json");

            EFlintRequest eFlintRequest =
                    new Gson().fromJson(request.body(), EFlintRequest.class);

            if(eFlintRequest.getUuid() == null) {
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR,"please provide a valid uuid"));
            }

            return new Gson().toJson(InstanceManager.getInstance().killInstance(eFlintRequest.getUuid()));

        });

        post("/kill_all", (request, response) -> {
            response.type("application/json");
            return new Gson().toJson(InstanceManager.getInstance().killAllInstances());
        });

        get("/get_all", (request, response) -> {
            response.type("application/json");
            return new Gson().toJson(InstanceManager.getInstance().getAll());
        });

    }
}