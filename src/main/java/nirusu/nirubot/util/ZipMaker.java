package nirusu.nirubot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipMaker {
    public static File compressFiles(Map<String, File> files, final String name, final File tmpDir) 
        throws IOException {        

        tmpDir.mkdir();

        File out = new File(tmpDir.getAbsolutePath() + File.separator + name);

        FileOutputStream fos = new FileOutputStream(out);

        ZipOutputStream zipOut = new ZipOutputStream(fos);

        for (String key : files.keySet()) {
            File f = files.get(key);
            FileInputStream fis = new FileInputStream(f);

            String[] arr = f.getName().split("\\.", -1);
            String fileEnding = key.endsWith(arr[arr.length - 1]) ? key : key + arr[arr.length -1];
            ZipEntry zipEntry = new ZipEntry(fileEnding);

            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }

        zipOut.close();
        fos.close();


        return out;
    }
}
