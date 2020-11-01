package nirusu.nirubot.command.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.command.ICommandContext;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.command.IPrivateCommand;
import nirusu.nirubot.command.PrivateCommandContext;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.util.ZipMaker;

public class YoutubeDl implements IPrivateCommand {
    private static final Pattern URL_REGEX = Pattern
            .compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    private static final Pattern OPTION_REGEX = Pattern.compile("-.+");

    private boolean asZip = false;
    private YoutubeDLRequest req;
    private boolean formatIsSet = false;

    enum Option {
        AUDIO("-audio") {
            @Override
            public void exec(YoutubeDl cmd) {
                if (cmd.formatIsSet)
                    throw new IllegalArgumentException("You can't use -audio and -video in one request");
                cmd.req.setOption("extract-audio");
                cmd.req.setOption("audio-format", "mp3");
                cmd.formatIsSet = true;
            }
        },
        VIDEO("-video") {
            @Override
            public void exec(YoutubeDl cmd) {
                if (cmd.formatIsSet)
                    throw new IllegalArgumentException("You can't use -audio and -video in one request");
                cmd.req.setOption("recode-video", "mp4");
                cmd.formatIsSet = true;
            }
        },
        ZIP("-zip") {
            @Override
            public void exec(YoutubeDl cmd) {
                cmd.asZip = true;
            }
        },
        BEST("-best") {
            @Override
            public void exec(YoutubeDl cmd) {
                cmd.req.setOption("format", "best");
            }
        };

        public abstract void exec(final YoutubeDl cmd);

        private final String option;

        Option(final String op) {
            this.option = op;
        }

        public static Option getOption(final String option) {
            for (Option o : Option.values()) {
                if (o.option.equals(option)) {
                    return o;
                }
            }
            throw new IllegalArgumentException(option + " is not an option");
        }
    }

    @Override
    public void execute(CommandContext ctx) {
        start(ctx);
    }    
    
    @Override
    public void execute(PrivateCommandContext ctx) {
        start(ctx);

    }

    @Override
    public List<String> alias() {
        return Collections.singletonList("ytd");
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("This command dowloads videos and playlists from youtube\n" + "Usage:\n`"
                + gm.prefix()
                + "ytd -<audio|video|zip|best> <link>` where `-<music|video|zip>` means `-music` or `-video` and `-zip` and `-best` "
                + "is an additional option that compresses all files into a zip (default for more then 5 files)"
                + "\nAn example would be: `" + gm.prefix() + "ytd -audio https://www.youtube.com/watch?v=5MRH-yfgxB0`",
                gm.prefix(), this);
    }

    @Override
    public MessageEmbed helpMessage() {
        return ICommand.createHelp("This command dowloads videos and playlists from youtube\n" + "Usage:\n`"
                + "ytd -<audio|video|zip|best> <link>` where `-<music|video|zip>` means `-music` or `-video` and `-zip` and `-best` "
                + "is an additional option that compresses all files into a zip (default for more then 5 files)"
                + "\nAn example would be: `" + "ytd -audio https://www.youtube.com/watch?v=5MRH-yfgxB0`",
                "", this);
    }

    public void start(final ICommandContext ctx) {
        List<String> args = ctx.getArgs();

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
                + File.separator + ctx.getAuthor().getId());

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
                    ctx.sendFile(f, f.getName());
                } else {
                    // hash map to zip later
                    files.put(f.getName(), f);
                }
            }

            if (!files.isEmpty()) {
                File zip;
                try {

                    // make zip
                    zip = ZipMaker.compressFiles(files, ctx.getAuthor().getId() + ".zip", Nirubot.getWebDir());

                    // if zip is not too big send directly
                    if (zip.length() <= ctx.getMaxFileSize()) {
                        ctx.getChannel().sendFile(zip).queue();
                    } else if (!asZip) {
                        // if user didnt want a zip but got one
                        ctx.reply(String.format(
                                "Some files were to big for discord, you can download them here: %s%s %s",
                                Nirubot.getHost() + Nirubot.getTmpDirPath(), zip.getName(),
                                ctx.getAuthor().getAsMention()));
                    } else {
                        ctx.reply(String.format("Here is your zip: %s%s %s",
                                Nirubot.getHost() + Nirubot.getTmpDirPath(), zip.getName(),
                                ctx.getAuthor().getAsMention()));
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
