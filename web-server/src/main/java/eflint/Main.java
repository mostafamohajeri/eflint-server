package eflint;

import com.google.gson.Gson;
import eflint.InstanceManager;
import eflint.utils.TemplateManager;
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
import java.io.File;
import java.util.*;
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
          File uploadDirectory = new File(System.getProperty("java.io.tmpdir"));
          response.type("application/json");
          request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(uploadDirectory.getAbsolutePath()));
          Part filePart = request.raw().getPart("fileToUpload");
            System.out.println("file uploaded: " + filePart.getSubmittedFileName());
          try (InputStream inputStream = request.raw().getPart("fileToUpload").getInputStream()) {
            File tempFile = File.createTempFile("uploaded-" , "-"+filePart.getSubmittedFileName());
            OutputStream outputStream = new FileOutputStream(tempFile);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            CreateEFlintInstanceRequest inst_req = new CreateEFlintInstanceRequest();
            inst_req.setModelName(tempFile.getAbsolutePath());
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

        if (args.length >= 1) {
          File file = new File(args[0]);
          if (file.isFile()) {
            List<String> paths = new ArrayList<String>();
            for (int i = 1; i < args.length; i++) 
              paths.add(args[1]);
            CreateEFlintInstanceRequest req = new CreateEFlintInstanceRequest();
            req.setModelName(args[0]);
            req.setFilePaths(paths);
            CompletableFuture<StandardResponse> r = InstanceManager.getInstance().createNewInstance(req);
          }
        }

    }
}
