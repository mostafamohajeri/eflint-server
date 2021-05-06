package eflint.utils;

import org.stringtemplate.v4.ST;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TemplateManager {
    private static TemplateManager ourInstance = new TemplateManager();

    public static TemplateManager getInstance() {
        return ourInstance;
    }

    private TemplateManager() {
    }

    public Optional<File> synthetize(String templateName, Map<String, String> values) {

        if(templateName == null)
            return Optional.empty();


        try {
            return Optional.of(
                    writeToFile(
                            synthesizeFile(
//                                    readTemplate(
//                                            templateName
//                                    ),
                                    Paths.get(templateName).toFile(),
                                    values !=null ? values : Collections.EMPTY_MAP
                            )
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();

    }

    private File readTemplate(String templateName) throws URISyntaxException {
        URL res = getClass().getClassLoader().getResource("templates/" + templateName);
        return Paths.get(res.toURI()).toFile();
    }

    private String synthesizeFile(File file, Map<String, String> values) throws IOException {

        InputStream is = new FileInputStream(file);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = buf.readLine()) != null) {
            sb.append(line).append("\n");
        }
        ST st = new ST(sb.toString());
        values.forEach(st::add
        );
        return (st).render();

    }


    public File writeToFile(String content)
            throws IOException {

        File tempFile = File.createTempFile("flint-file-", ".eflint");
        System.out.println("Temp file On Default Location: " + tempFile.getAbsolutePath());
        Files.write(tempFile.toPath(), List.of(content), StandardCharsets.UTF_8);
        return tempFile;

    }

    private String generateFileName() {
        return UUID.randomUUID().toString() + ".eflint";
    }


    public static void main(String[] args) {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("person_name", "\"Mss  ss E\"");

        System.out.println(TemplateManager.getInstance().synthetize("bidding_desire.eflint", map));
    }
}
