package nirusu.nirubot.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import nirusu.nirubot.Nirubot;

public final class IndexCreator {
    private static String index = null;

    private IndexCreator() {
    }

    public static String getIndex() throws IOException {
        if (index == null) {

            try (var in = new BufferedReader(new FileReader(
                    new File(System.getProperty("user.dir").concat(File.separator).concat("index.html"))))) {

                StringBuilder sb = new StringBuilder();

                String line = null;

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                index = sb.toString();
            }
            if (index == null)
                throw new IOException("Example Index not found");
        }

        return index;
    }

    public static void createIndex(File dir) throws IOException {
        if (!dir.isDirectory()) {
            return;
        }

        String example = IndexCreator.getIndex();

        File indexFile = new File(dir.getAbsolutePath() + File.separator + "index.html");
        Files.createFile(indexFile.toPath());

        StringBuilder content = new StringBuilder();

        File zip = null;

        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".zip")) {
                zip = f;
            } else if (!f.getName().endsWith(".html")) {
                String[] arr = f.toURI().toString().split(dir.toString());
                content.append("<dt><a href=\"./").append(arr[1]).append("\">").append(f.getName()).append("</a></dt>");
            }
        }

        example = example.replace("LIST_CONTENT", content.toString());

        if (zip != null) {
            example = example.replace("ZIP_NAME", zip.getName());
        }

        try (FileWriter writer = new FileWriter(index)) {
            writer.write(example);
            writer.flush();
        } catch (IOException e) {
            Nirubot.error(e.getMessage(), e);
        }
    }

}
