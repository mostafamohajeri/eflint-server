package eflint;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.*;
import eflint.utils.TemplateManager;
import requesthandlers.EFlintRequestHandler;
import requests.ControlRequest;
import requests.CreateEFlintInstanceRequest;
import requests.EFlintRequest;
import response.ListContainer;
import response.StandardResponse;
import response.StatusResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static requesthandlers.EFlintRequestHandler.handleRequest;

public class InstanceManager {
    private static InstanceManager ourInstance = new InstanceManager();

    public static InstanceManager getInstance() {
        return ourInstance;
    }

    private InstanceManager() {
        instances = new ConcurrentHashMap<>();
    }


    // limit of instances
    private int limit = 10;
    private Map<String, EFlintInstance> instances;
    private static final int PORT_MIN_NUM = 20000;
    private static final int PORT_MAX_NUM = 40000;
    // how to run eflint-server instance
    private static final String EFLINT_COMMAND = "eflint-server";
    private static final String FLINT_READY_MESSAGE = "AWAITING STATEMENT";


    public StandardResponse getAll() {
        return new StandardResponse(
                StatusResponse.SUCCESS, new Gson().toJsonTree(ListContainer.from(new ArrayList<>(instances.values())))
        );
    }


    public int getPortByUUID(String uuid) {
        if (instances.containsKey(uuid)) {
            return instances.get(uuid).getPort();
        }
        return -1;
    }

    public synchronized StandardResponse killInstance(String uuid) {

        Gson gson = new Gson();

        EFlintInstance e = instances.remove(uuid);

        EFlintRequest eFlintRequest = ControlRequest.crateKillCommand(uuid);

        StandardResponse r = EFlintRequestHandler.handleRequest(eFlintRequest,e.getPort());

        e.getThread().interrupt();

        return r;
    }


    public synchronized StandardResponse killAllInstances() {

        Gson gson = new Gson();

        JsonArray a = new JsonArray();

        for (EFlintInstance instance : instances.values()
        ) {
            EFlintRequest eFlintRequest = ControlRequest.crateKillCommand(instance.getUuid());

            StandardResponse r = EFlintRequestHandler.handleRequest(eFlintRequest,instance.getPort());

            instance.getThread().interrupt();

            a.add(r.getData());
        }

        instances.clear();




        return new StandardResponse(StatusResponse.SUCCESS, a);

    }

    public synchronized CompletableFuture<StandardResponse> createNewInstance(CreateEFlintInstanceRequest request) {
        int port = getRandomPort();

        CompletableFuture<StandardResponse> futureResponse = new CompletableFuture<>();
        Optional<File> opFlintFile;
        if (request.getValues() == null) {
          File file = new File(request.getModelName());
          opFlintFile = file.isFile() ? Optional.of(file) : Optional.empty();
        }
        else {
          opFlintFile = TemplateManager.getInstance().synthetize(request.getModelName(),request.getValues());
       }

        String sourceFileName = request.getModelName();

        List<String> paths = new ArrayList<>();

        if(Objects.nonNull(request.getFilePaths()))
            paths.addAll(request.getFilePaths().stream().map(File::new).filter(File::isDirectory).map(File::getAbsolutePath).collect(Collectors.toList()));

        if(opFlintFile.isEmpty()) {
            futureResponse.complete(new StandardResponse(StatusResponse.ERROR, "something went wrong with synthesizing your template"));
        } else {


            String uuid = generateUUID();

            Thread t = new Thread(() -> {

                if (instances.keySet().size() >= limit) {
                    futureResponse.complete(new StandardResponse(StatusResponse.ERROR, "limit of " + limit + " instances reached"));
                    return;
                }

                runEFlintProcess(opFlintFile.get(), port, uuid , paths, new EFlintLister() {
                    @Override
                    public void started() {
                        System.out.println(String.format("instance started on port %s with uuid %s",port,uuid));
                        instances.put(uuid, EFlintInstance.from(port, uuid, Thread.currentThread(),sourceFileName, paths , Timestamp.from(Instant.now())));
                        futureResponse.complete(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(instances.get(uuid))));

                    }

                    @Override
                    public void terminated(int exitVal, String uuid) {
                        EFlintInstance e = instances.remove(uuid);

                        if (e != null) {
                            e.getThread().interrupt();
                            futureResponse.complete(new StandardResponse(StatusResponse.ERROR, "flint exit code: " + exitVal));
                        }
                    }
                });
            }
            );

            t.start();
        }

        return futureResponse;

    }


    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private int getRandomPort() {

        int port = -1;

        Random r = new Random();

        while (port == -1) {

            int p = PORT_MIN_NUM + r.nextInt(PORT_MAX_NUM - PORT_MIN_NUM);

            if (available(p))
                port = p;

        }

        return port;
    }

    private boolean available(int port) {
        if (port < PORT_MIN_NUM || port > PORT_MAX_NUM) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }


    private void runEFlintProcess(File file,int port, String uuid,List<String> filePaths, EFlintLister lister) {
//        ProcessBuilder processBuilder = new ProcessBuilder();

        // -- Linux --


        int exitVal = -1;
        // Run a shell command
//        processBuilder.command("bash" ,"-c" , "ls -ali" );

        try {

            // -- Linux --

            // Run a shell command
//            Process proc = Runtime.getRuntime().exec(new String[]{"bash","-c", });
            List<String> command = new ArrayList<String>();
            command.add(EFLINT_COMMAND);
            command.add(file.getAbsolutePath());
            command.add(String.valueOf(port));
            for (String filePath : filePaths) {
              command.add("-i"); command.add(filePath);
            }
            ProcessBuilder ps = new ProcessBuilder(command);
            System.out.println(ps.command().stream().collect(Collectors.joining(" ")));
            ps.redirectErrorStream(true);
//            ps.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            Process pr = ps.start();


            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            lister.started();

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("here:" + line);
            }
            pr.waitFor();
            System.out.println("ok!");

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lister.terminated(exitVal, uuid);

    }

    public interface EFlintLister {

        void started();

        void terminated(int exitVal, String uuid);
    }


}
