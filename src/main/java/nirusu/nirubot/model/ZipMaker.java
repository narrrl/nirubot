package nirusu.nirubot.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipMaker {

    private ZipMaker() {
        throw new IllegalAccessError();
    }

    public static File compressFiles(List<File> files, final String name, final File tmpDir) throws IOException {

        if (!tmpDir.exists()) tmpDir.mkdirs();

        File out = new File(tmpDir.getAbsolutePath() + File.separator + name);

        if (out.exists()) Files.delete(out.toPath());

        Files.createFile(out.toPath());

        FileOutputStream fos = new FileOutputStream(out);

        try (ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            for (File f : files) {
                try (FileInputStream fis = new FileInputStream(f)) {
                    ZipEntry zipEntry = new ZipEntry(f.getName());

                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }

                }
            }
        }
        fos.close();

        return out;
    }
}
