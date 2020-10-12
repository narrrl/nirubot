package nirusu.nirubot.command.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.util.ZipMaker;

public class EmoteDownloader implements ICommand {

    @Override
    public void execute(final CommandContext ctx) {

        if (ctx.getArgs().size() != 1) {
            return;
        }

        HashMap<String, File> files = new HashMap<>();
        int i = 1;
        for (Emote e : ctx.getGuild().getEmotes()) {
            try {
                InputStream in = new URL(e.getImageUrl()).openStream();
                String[] arr = e.getImageUrl().split("/");
                URI uri = new URI(System.getProperty("user.dir") + File.separator + arr[arr.length - 1]);
                File f = new File(uri.getPath());
                Files.copy(in, Paths.get(uri.getPath()), StandardCopyOption.REPLACE_EXISTING);
                String key = e.getName();

                if (files.containsKey(key)) {
                    files.put(key + i, f);
                    i++;
                } else {
                    files.put(key, f);
                }

            } catch (IOException | URISyntaxException err) {
                Nirubot.warning(err.getMessage());
            }
        }

        List<File> zips = new ArrayList<>();

        try {
            if (totalSizeOf(files.values()) <= 8000000)
                zips.add(ZipMaker.compressFiles(files, 0));
            else {
                Map<String, File> tmpList = new HashMap<>();
                int it = 0;
                for (String key : files.keySet()) {
                    File f = files.get(key);

                    if (totalSizeOf(tmpList.values()) + f.length() > 8000000) {
                        zips.add(ZipMaker.compressFiles(tmpList, it));
                        tmpList = new HashMap<>();
                        it++;
                    }

                    tmpList.put(key, f);
                }

                if(!tmpList.isEmpty()) {
                    zips.add(ZipMaker.compressFiles(tmpList, it));
                }

            }
        } catch (IOException e) {
            ctx.reply(e.getMessage());
            for (File f : files.values()) {
                f.delete();
            }
            return;
        }

        for (File f : files.values()) {
            f.delete();
        }

        try {
            for (File zip : zips) {
                ctx.getEvent().getChannel().sendFile(zip, "emotes.zip").queue();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        zips.forEach(File::delete);
    }

    private long totalSizeOf(Collection<File> values) {
        long total = 0;

        for (File f : values) {
            total += f.length();
        }
        return total;
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("ed", "edown");
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return null;
    }
    
}
