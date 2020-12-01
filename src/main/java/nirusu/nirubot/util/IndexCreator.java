package nirusu.nirubot.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class IndexCreator {
    private static final String FIRST_HALF = "<!DOCTYPE html><html lang=\"en-US\"><head profile=\"https://nirusu99.de/\">"
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
        
        index.createNewFile();

        String content = FIRST_HALF + "<ul style=\"line-height:1.5\">";

        File zip = null;

        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".zip")) {
                zip = f;
            } else if (!f.getName().endsWith(".html")) {
                content += "<li><a href=\"./" + f.getName() + "\">" + f.getName() + "</a></li>";
            }
        }

        content += "</ul>";

        if (zip != null) {
            content += "<a href=\"./" + zip.getName() + "\">Download all!</a>";
        }

        content += SECOND_HALF;

        FileWriter writer = new FileWriter(index);
        writer.write(content);
        writer.flush();
        writer.close();
    }

    
}
