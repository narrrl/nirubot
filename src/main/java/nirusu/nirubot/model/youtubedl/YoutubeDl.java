package nirusu.nirubot.model.youtubedl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;

import nirusu.nirubot.Nirubot;
import nirusu.nirubot.model.IndexCreator;
import nirusu.nirubot.model.RandomString;
import nirusu.nirubot.model.ZipMaker;
import nirusu.nirucmd.CommandContext;

public class YoutubeDl {
    private static final long MAX_FILE_SIZE = 8388119;
    private static final Pattern URL_REGEX = Pattern
            .compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    private static final Pattern OPTION_REGEX = Pattern.compile("-.+");

    private final YoutubeDLRequest req;
    private final String randomString;
    private final File tmpDir;
    private final File webDir;

    public YoutubeDl(List<String> args) throws InvalidYoutubeDlException {
        String videoURL = getURL(args);
        this.randomString = RandomString.getRandomString(4);
        this.tmpDir = new File(Nirubot.getTmpDirectory().getAbsolutePath().concat(File.separator) + "youtube-dl"
                + File.separator + randomString);
        this.tmpDir.mkdirs();
        this.webDir = new File(Nirubot.getWebDir().getAbsolutePath() + File.separator + randomString);
        this.webDir.mkdirs();

        this.req = new YoutubeDLRequest(videoURL, tmpDir.getAbsolutePath());
        setOptions(Collections.emptyList(), req);
    }

    public YoutubeDl(String[] args) throws InvalidYoutubeDlException {
        String videoURL = getURL(List.of(args));
        this.randomString = RandomString.getRandomString(4);
        this.tmpDir = new File(Nirubot.getTmpDirectory().getAbsolutePath().concat(File.separator).concat("teamspeak").concat(File.separator).concat("youtube-dl").concat(File.separator).concat(randomString));
        this.tmpDir.mkdirs();
        this.webDir = tmpDir;

        this.req = new YoutubeDLRequest(videoURL, tmpDir.getAbsolutePath());
        setOptions(List.of(args), req);
    }

    public File start() throws InvalidYoutubeDlException {
        // download files
        try {
            YoutubeDL.execute(req);
        } catch (YoutubeDLException e) {
            throw new InvalidYoutubeDlException(e.getMessage());
        }

        // return if file can be send directly
        if (tmpDir.listFiles().length == 1 && tmpDir.listFiles()[0].length() < MAX_FILE_SIZE) {
            return tmpDir.listFiles()[0];
        }

        createZip();

        // now move all other files to the web dir
        for (File f : tmpDir.listFiles()) {
            File t = new File(webDir.getAbsolutePath() + File.separator + f.getName());
            try {
                Files.move(f.toPath(), t.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // create index.html in that directory
        try {
            IndexCreator.createIndex(webDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return webDir;
    }

    public File getFile() throws InvalidYoutubeDlException {
        try {
            YoutubeDL.execute(req);
        } catch (YoutubeDLException e) {
            throw new InvalidYoutubeDlException(e.getMessage());
        }

        if (tmpDir.listFiles().length == 1) {
            return tmpDir.listFiles()[0];
        } else {

            // iterate through all the downloaded files
            File zip;
            try {
                // make zip
                zip = ZipMaker.compressFiles(List.of(tmpDir.listFiles()), randomString + ".zip", tmpDir);
            } catch (IOException e) {
                throw new InvalidYoutubeDlException(e.getMessage());
            }
            return zip;
        }

    }


    private void createZip() throws InvalidYoutubeDlException {
        if (tmpDir.listFiles().length > 1) {
            // hashmap for zip
            List<File> files = new ArrayList<>();

            // iterate through all the downloaded files
            Collections.addAll(files, tmpDir.listFiles());

            if (files.isEmpty()) {
                throw new InvalidYoutubeDlException("Nothing downloaded!");
            }
            try {
                // make zip
                ZipMaker.compressFiles(files, randomString + ".zip", webDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void setOptions(List<String> args, YoutubeDLRequest request) throws InvalidYoutubeDlException {
        // default options
        request.setOption("add-metadata");
        request.setOption("age-limit", 69); //nice
        request.setOption("quiet");
        request.setOption("no-warnings");
        request.setOption("ignore-errors");
        request.setOption("output", "%(title).90s.%(ext)s"); // limit output length

        // specific options by user
        boolean specificFormat = false;
        for (String arg : args) {
            // if the given arg matches the regex for an option
            if (YoutubeDl.OPTION_REGEX.matcher(arg).matches()) {
                // set option
                Option op = Option.getOption(arg);
                if (op.equals(Option.AUDIO) || op.equals(Option.VIDEO)) {
                    if (!specificFormat) {
                        specificFormat = true;
                    } else {
                        throw new InvalidYoutubeDlException("You can't use -audio and -video in one request");
                    }
                }
                op.exec(request);
            }
        }

    }

    private String getURL(List<String> args) throws InvalidYoutubeDlException {
        String url = null;
        int offset = 0;
        for (String arg : args) {
            if (YoutubeDl.URL_REGEX.matcher(arg).matches()) {
                url = arg;
                offset++;
                if (offset > 1) {
                    throw new InvalidYoutubeDlException(
                            "You can download only from one source at a time (Remove one of the links)");
                }
            }
        }

        if (url == null) {
            throw new InvalidYoutubeDlException("Provide a link to download");
        }

        return url;
    }

}
