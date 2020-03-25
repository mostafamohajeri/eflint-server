package eflint;

import com.google.gson.Gson;
import response.StandardResponse;
import response.StatusResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

public class InstanceManager {
    private static InstanceManager ourInstance = new InstanceManager();

    public static InstanceManager getInstance() {
        return ourInstance;
    }

    private InstanceManager() {
        instances = new ConcurrentHashMap<>();
    }


    // limit of instances
    private int limit = 3;
    private Map<String, EFlintInstance>     instances;
    private static final int PORT_MIN_NUM = 20000;
    private static final int PORT_MAX_NUM = 30000;
    // how to run eflint-server instance
    private static final String EFLINT_COMMAND = "eflint-server";
    // eflint model file address
    private static final String EFLINT_FILE = "/home/msotafa/IdeaProjects/language-docs/flint/eflintonline/examples/voting_full.eflint";


    public StandardResponse getAll() {
        return new StandardResponse(StatusResponse.SUCCESS,new Gson().toJsonTree(new ArrayList<>(instances.keySet())));
    }

    public int getPortByUUID(String uuid) {
        if( instances.containsKey(uuid) ) {
            return instances.get(uuid).getPort();
        }
        return -1;
    }

    public synchronized StandardResponse killInstance(String uuid) {


        EFlintInstance e = instances.remove(uuid);

        if(e != null) {
            e.getThread().stop();
            return new StandardResponse(StatusResponse.SUCCESS, "flint exited nicely :)");
        }

        return new StandardResponse(StatusResponse.ERROR, "not found");
    }


    public synchronized StandardResponse killAllInstances() {
        for (EFlintInstance instance: instances.values()
             ) {
            instance.getThread().stop();
        }

        instances.clear();

        return new StandardResponse(StatusResponse.SUCCESS);
    }

    public synchronized CompletableFuture<StandardResponse> createNewInstance() {
        int port = getRandomPort();

        CompletableFuture<StandardResponse> futureResponse = new CompletableFuture<>();

        String uuid = generateUUID();

        Thread t = new Thread(() -> {

            if (instances.keySet().size() >= limit) {
                futureResponse.complete(new StandardResponse(StatusResponse.ERROR, "limit of " + limit + " instances reached"));
                return;
            }

            runEFlintProcess(port,uuid, new EFlintLister() {
                @Override
                public void started() {
                    instances.put(uuid,new EFlintInstance(port,uuid,Thread.currentThread()));
                    futureResponse.complete(new StandardResponse(StatusResponse.SUCCESS, uuid));
                }

                @Override
                public void terminated(int exitVal,String uuid) {
                    EFlintInstance e = instances.remove(uuid);

                    if(e != null) {
                        e.getThread().stop();
                        futureResponse.complete(new StandardResponse(StatusResponse.ERROR, "flint exit code: " + exitVal));
                    }
                }
            });
        }
        );

        t.start();

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


    private void runEFlintProcess(int port,String uuid, EFlintLister lister) {
//        ProcessBuilder processBuilder = new ProcessBuilder();

        // -- Linux --

        int exitVal = -1;
        // Run a shell command
//        processBuilder.command("bash" ,"-c" , "ls -ali" );

        String command = EFLINT_COMMAND + " " + EFLINT_FILE + " " + String.valueOf(port);
//        System.out.println(command);
//        processBuilder.command("bash" ,"-c" , EFLINT_COMMAND + " " + EFLINT_FILE + " "  + String.valueOf(port));

        try {

            // -- Linux --

            // Run a shell command
            Process proc = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            lister.started();

            String s;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            exitVal = proc.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lister.terminated(exitVal,uuid);

    }

    public interface EFlintLister {
        void started();
        void terminated(int exitVal,String uuid);
    }



}
