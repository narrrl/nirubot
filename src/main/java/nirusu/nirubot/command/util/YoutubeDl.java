package nirusu.nirubot.command.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.util.ZipMaker;

public class YoutubeDl implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        List<String> args = ctx.getArgs();

        if (args.size() < 3) {
            return;
        }

        String videoURL = args.get(2);

        File tmpDir = new File(Nirubot.getTmpDirectory().getAbsolutePath().concat(File.separator)
                + ctx.getGuild().getIdLong() + File.separator + "youtube-dl" + File.separator + ctx.getAuthor().getId());

        tmpDir.mkdirs();

        if (tmpDir.listFiles().length != 0) {
            ctx.reply("You can only request one download at a time (per server lol)");
            return;
        }

        YoutubeDLRequest req = new YoutubeDLRequest(videoURL, tmpDir.getAbsolutePath());

        if (args.get(1).equals("-music")) {
            req.setOption("extract-audio");
            req.setOption("audio-format", "mp3");
        } else if (args.get(1).equals("-video")) {
            req.setOption("recode-video", "mp4");
        } else {
            return;
        }
        req.setOption("add-metadata");
        req.setOption("age-limit", 30);


        ctx.reply("Started, can take some times if the playlist is big or if you download videos in general");
        
        new Thread() {
            @Override
            public void run() {
                try {
                    YoutubeDL.execute(req);
                } catch (YoutubeDLException e) {
                    ctx.reply(e.getMessage());
                }
                boolean asZip = false;

                if (args.size() == 4 && args.get(3).equals("-zip")) asZip = true;

                if (tmpDir.listFiles().length > 5) asZip = true;

                HashMap<String, File> files = new HashMap<>();
                for (File f : tmpDir.listFiles()) {
                    if (!asZip && f.length() <= ctx.getGuild().getMaxFileSize()) {
                        ctx.getEvent().getChannel().sendFile(f, f.getName()).complete();
                    } else {
                        files.put(f.getName(), f);
                    }
                }
                if (!files.isEmpty()) {
                    File zip;
                    try {
                        zip = ZipMaker.compressFiles(files, ctx.getAuthor().getId() + ".zip", Nirubot.getWebDir());
                        if (!asZip) ctx.reply("Some files were to big for discord, you can download them here: https://nirusu99.de/discord/tmp/" + zip.getName());
                        else ctx.reply("Here is your zip: https://nirusu99.de/discord/tmp/" + zip.getName() + " " + ctx.getAuthor().getAsMention());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                for (File f : tmpDir.listFiles()) {
                    f.delete();
                }
            }
        }.start();
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("ytd");
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("This command dowloads videos and playlists from youtube\n"
        + "Usage:\n`" + gm.prefix() + "ytd -<music|video> <link> ?<-zip>` where `-<music|video>` means `-music` or `-video` and `?<-zip>` indicates an optional tag to compress everything in a zip."
        + "\nAn example would be: `" + gm.prefix() + "ytd -music https://www.youtube.com/watch?v=5MRH-yfgxB0`", gm.prefix(), this);
    }
    
}
