package nirusu.nirubot.util.youtubedl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import discord4j.core.object.entity.User;
import nirusu.nirubot.Nirubot;
import nirusu.nirucmd.CommandContext;

public final class YoutubeDLThread extends Thread {
    private static final long FILE_SIZE_LIMIT = 1000000000;
    private CommandContext ctx;
    private List<String> args;
    private User author;
    private boolean isRunning;

    public YoutubeDLThread(CommandContext ctx, List<String> args, User author) {
        super("Download for: " + author.getUsername());
        this.ctx = ctx;
        this.args = args;
        this.author = author;
    }

    @Override
    public void run() {
        this.isRunning = true;
        File out;
        try {
            out = new YoutubeDl(args).start();
        } catch (InvalidYoutubeDlException e) {
            this.isRunning = false;
            if (e.getMessage() != null) {
                ctx.reply(e.getMessage());
            } else {
                e.printStackTrace();
            }
            return;
        }
        this.isRunning = false;
        if (computeFileSize(out) > FILE_SIZE_LIMIT) {
            ctx.reply("Your download was over 1 gigabyte and gets deleted after 10 Minutes!");
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(() -> {
                File f = out;
                Nirubot.info("Deleting Folder: " + out.getName());
                try {
                    Nirubot.deleteRecursive(f);
                } catch (IOException e) {
                    Nirubot.warning(e.getMessage());
                }
            }, 10, TimeUnit.MINUTES);
        }
        if (out.length() > CommandContext.getMaxFileSize() || out.isDirectory()) {
            ctx.reply(String.format("You can download %s here: %s%s %s", out.getName(),
                    Nirubot.getHost() + Nirubot.getTmpDirPath(), out.getName(), author.getMention()));
            return;
        }
        ctx.sendFile(out);
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    private long computeFileSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            // be careful if you have more than 9 exabytes of storage. Could produce
            // overflow then.
            size = Stream.of(f.listFiles()).map(File::length).reduce(0L, (a, b) -> a + b);
        } else {
            size = f.length();
        }
        return size;
    }
}
