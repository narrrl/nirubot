package nirusu.nirubot.util.youtubedl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;

import discord4j.core.object.entity.User;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.util.ZipMaker;
import nirusu.nirucmd.CommandContext;

public class YoutubeDl {
    private static final Pattern URL_REGEX = Pattern
            .compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    private static final Pattern OPTION_REGEX = Pattern.compile("-.+");

    boolean asZip = false;
     YoutubeDLRequest req;
    boolean formatIsSet = false; 

    public void start(final CommandContext ctx) throws InvalidYoutubeDlException {
        List<String> args = ctx.getArgs().orElseThrow(InvalidYoutubeDlException::new);

        if (args.size() < 2) {
            return;
        }

        String videoURL = null;

        int offset = 0;
        for (String arg : args) {
            if (YoutubeDl.URL_REGEX.matcher(arg).matches()) {
                videoURL = arg;
                offset++;
                if (offset > 1) {
                    ctx.reply("You can download only from one source at a time (Remove one of the links)");
                    return;
                }
            }
        }

        if (videoURL == null) {
            ctx.reply("Provide a link to download!");
            return;
        }

        // stores in /tmp/youtube-dl/{userid}
        File tmpDir = new File(Nirubot.getTmpDirectory().getAbsolutePath().concat(File.separator) + "youtube-dl"
                + File.separator + ctx.getAuthor().orElseThrow().getId().asLong());

        tmpDir.mkdirs();

        // if there are files already -> user is already converting something
        if (tmpDir.listFiles().length != 0) {
            ctx.reply("You can only request one download at a time");
            return;
        }

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
                    ctx.reply(e.getMessage());
                    return;
                }
            }
        }

        ctx.reply("Started, can take some time if the playlist is big or if you download videos in general");

        // dumb work to a new thread that the bot wont get blocked

        User author = ctx.getAuthor().orElseThrow(InvalidYoutubeDlException::new);
        new Thread(() -> {

            // download files
            try {
                YoutubeDL.execute(req);
            } catch (YoutubeDLException e) {
                ctx.reply(e.getMessage());
            }

            // always zip with more then 5 files
            if (tmpDir.listFiles().length > 5)
                asZip = true;

            // hashmap for zip
            HashMap<String, File> files = new HashMap<>();

            // iterate through all the downloaded files
            for (File f : tmpDir.listFiles()) {
                if (!asZip && f.length() <= ctx.getMaxFileSize()) {
                    // send files directly
                    ctx.sendFile(f);
                } else {
                    // hash map to zip later
                    files.put(f.getName(), f);
                }
            }

            if (!files.isEmpty()) {
                File zip;
                try {

                    // make zip
                    zip = ZipMaker.compressFiles(files, author
                        .getId() + ".zip", Nirubot.getWebDir());

                    // if zip is not too big send directly
                    if (zip.length() <= ctx.getMaxFileSize()) {
                        ctx.sendFile(zip);
                    } else if (!asZip) {
                        // if user didnt want a zip but got one
                        ctx.reply(String.format(
                                "Some files were to big for discord, you can download them here: %s%s %s",
                                Nirubot.getHost() + Nirubot.getTmpDirPath(), zip.getName(),
                                author.getMention()));
                    } else {
                        ctx.reply(String.format("Here is your zip: %s%s %s",
                                Nirubot.getHost() + Nirubot.getTmpDirPath(), zip.getName(),
                                author.getMention()));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (File f : tmpDir.listFiles()) {
                f.delete();
            }
        }).start(); // there he goes
    }
    
}
