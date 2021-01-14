package eflint;

import com.google.gson.Gson;
import eflint.InstanceManager;
import requesthandlers.EFlintRequestHandler;
import requests.CreateEFlintInstanceRequest;
import requests.EFlintRequest;
import response.StandardResponse;
import response.StatusResponse;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;

import spark.utils.IOUtils;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        port(8080);

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

            System.out.println(createEFlintInstanceRequest);

            System.out.println(new Gson().toJson(createEFlintInstanceRequest));

            CompletableFuture<StandardResponse> r = InstanceManager.getInstance().createNewInstance(createEFlintInstanceRequest);

            return new Gson().toJson(r.get());

        });

        post("/upload", (request, response) -> {
          response.type("application/json");
          request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp"));
          Part filePart = request.raw().getPart("fileToUpload");
          try (InputStream inputStream = request.raw().getPart("fileToUpload").getInputStream()) {
            String target_file = "/tmp/" + filePart.getSubmittedFileName();
            OutputStream outputStream = new FileOutputStream(target_file);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            CreateEFlintInstanceRequest inst_req = new CreateEFlintInstanceRequest();
            inst_req.setModelName(target_file);
            CompletableFuture<StandardResponse> r = InstanceManager.getInstance().createNewInstance(inst_req);
            return new Gson().toJson(r.get());
         }

        });

        post("/kill", (request, response) -> {

            response.type("application/json");

            EFlintRequest eFlintRequest =
                    new Gson().fromJson(request.body(), EFlintRequest.class);


            if(eFlintRequest.getUuid() == null) {
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR,"please provide a valid uuid"));
            }

            int port = InstanceManager.getInstance().getPortByUUID(eFlintRequest.getUuid());

            if(port == -1) {
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR,"no such instance present"));
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
