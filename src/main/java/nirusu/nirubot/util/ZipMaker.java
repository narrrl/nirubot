package nirusu.nirubot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipMaker {
    public static File compressFiles(Map<String, File> files, int num) 
        throws IOException {

        File out = new File(String.format("emotes%d.zip", num));

        FileOutputStream fos = new FileOutputStream(out);

        ZipOutputStream zipOut = new ZipOutputStream(fos);

        for (String key : files.keySet()) {
            File f = files.get(key);
            FileInputStream fis = new FileInputStream(f);

            ZipEntry zipEntry = new ZipEntry(key + "." + f.getName().split("\\.", -1)[1]);

            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();

            if (out.length() + f.length() > 8000000) {
                zipOut.close();
                fos.close();
                return out;
            }
        }

        zipOut.close();
        fos.close();


        return out;
    }
}
