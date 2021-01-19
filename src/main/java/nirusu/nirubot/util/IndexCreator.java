package nirusu.nirubot.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import nirusu.nirubot.Nirubot;

public final class IndexCreator {
    private static final String FIRST_HALF = "<!DOCTYPE html><html><head profile=\"https://nirusu99.de/\"><meta charset=\"utf-16\">"
    + "<link rel=\"icon\" type=\"image/png\" href=\"/img/favicon.png\"><link rel=\"stylesheet\" href=\"/css/style.css\"><html>"
    + "<head><title>Home - Nirusu99.de</title></head><body><div class=\"topnav\"><a class=\"active\" href=\"/\">Home</a>"
    + "<a href=\"/videos\">Videos</a><a href=\"http://nirusu99.de:8123/\">Minecraft Server Map</a><a href=\"/uni\">Uni-Material</a>"
    + "<a href=\"/downloads\">Downloads</a></div>";
    private static final String SECOND_HALF = "</body></html>";

    private IndexCreator() {
    }

    public static void createIndex(File dir) throws IOException {
        if (!dir.isDirectory()) {
            return;
        }

        File index = new File(dir.getAbsolutePath() + File.separator + "index.html");
        
        Files.createFile(index.toPath());

        StringBuilder content = new StringBuilder().append(FIRST_HALF + "<ul style=\"line-height:1.5\">");

        File zip = null;

        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".zip")) {
                zip = f;
            } else if (!f.getName().endsWith(".html")) {
                String[] arr = f.toURI().toString().split(dir.toString());
                content.append("<li><a href=\".").append(arr[1]).append("\">").append(f.getName()).append("</a></li>");
            }
        }

        content.append("</ul>");

        if (zip != null) {
            content.append("<a href=\"./").append(zip.getName()).append("\">Download all!</a>");
        }

        content.append(SECOND_HALF);

        try (FileWriter writer = new FileWriter(index)) {
            writer.write(content.toString());
            writer.flush();
        } catch (IOException e) {
            Nirubot.error(e.getMessage(), e);
        }
    }

    
}
