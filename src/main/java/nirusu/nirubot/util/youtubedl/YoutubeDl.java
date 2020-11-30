package nirusu.nirubot.util.youtubedl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;

import nirusu.nirubot.Nirubot;
import nirusu.nirubot.util.RandomString;
import nirusu.nirubot.util.ZipMaker;
import nirusu.nirucmd.CommandContext;

public class YoutubeDl {
    private static final Pattern URL_REGEX = Pattern
            .compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    private static final Pattern OPTION_REGEX = Pattern.compile("-.+");

    boolean asZip = false;
     YoutubeDLRequest req;
    boolean formatIsSet = false; 

    public File start(final List<String> args) throws InvalidYoutubeDlException {

        String videoURL = null;

        int offset = 0;
        for (String arg : args) {
            if (YoutubeDl.URL_REGEX.matcher(arg).matches()) {
                videoURL = arg;
                offset++;
                if (offset > 1) {
                    throw new InvalidYoutubeDlException("You can download only from one source at a time (Remove one of the links)");
                }
            }
        }

        if (videoURL == null) {
            throw new InvalidYoutubeDlException("Provide a link to download");
        }

        String randomString = RandomString.getRandomString(4);
        // stores in /tmp/youtube-dl/{userid}
        File tmpDir = new File(Nirubot.getTmpDirectory().getAbsolutePath().concat(File.separator) + "youtube-dl"
                + File.separator + randomString);

        tmpDir.mkdirs();


        Nirubot.cleanDir(tmpDir);

        this.req = new YoutubeDLRequest(videoURL, tmpDir.getAbsolutePath());

        // default options
        req.setOption("add-metadata");
        req.setOption("age-limit", 69);
        req.setOption("quiet");
        req.setOption("no-warnings");
        req.setOption("ignore-errors");

        // specific options by user
        for (String arg : args) {
            // if the given arg matches the regex for an option
            if (YoutubeDl.OPTION_REGEX.matcher(arg).matches()) {
                try {
                    // set option
                    Option.getOption(arg).exec(this);
                } catch (IllegalArgumentException e) {
                    // if option doesnt exist, inform user
                    throw new InvalidYoutubeDlException(e.getMessage());
                }
            }
        }

        // dumb work to a new thread that the bot wont get blocked
        // download files
        try {
            YoutubeDL.execute(req);
        } catch (YoutubeDLException e) {
            throw new InvalidYoutubeDlException(e.getMessage());
        }


        // zip if more then
        if (tmpDir.listFiles().length == 1 
            && tmpDir.listFiles()[0].length() < CommandContext.getMaxFileSize()) {                    
            return tmpDir.listFiles()[0];
        }

        File dir = new File(Nirubot.getWebDir().getAbsolutePath() + File.separator + randomString);

        dir.mkdirs();

        for (File f : tmpDir.listFiles()) {
            File t = new File(dir.getAbsolutePath() + File.separator + f.getName());
            try {
                Files.move(f.toPath(), t.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // hashmap for zip
        HashMap<String, File> files = new HashMap<>();

        // iterate through all the downloaded files
        for (File f : tmpDir.listFiles()) {
            // hash map to zip later
            files.put(f.getName(), f);
        }

        File zip;
        if (files.isEmpty()) {
            throw new InvalidYoutubeDlException("Nothing downloaded!");
        }
        try {
            // make zip
            zip = ZipMaker.compressFiles(files, randomString + ".zip", dir);
        } catch (IOException e) {
            throw new InvalidYoutubeDlException(e.getMessage());
        }

        return dir;
    }
    
}
