package nirusu.nirubot.model.youtubedl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import nirusu.nirubot.Nirubot;
import nirusu.nirucmd.CommandContext;

public final class YoutubeDLHandler {
    private static final File TMP_DIR = new File(
            Nirubot.getTmpDirectory().getAbsolutePath().concat(File.separator) + "youtube-dl");
    private static YoutubeDLHandler handler;
    private Map<Snowflake, YoutubeDLThread> usersDownloading;

    public static YoutubeDLHandler getInstance() {
        if (handler == null) {
            handler = new YoutubeDLHandler();
        }
        return handler;
    }

    private YoutubeDLHandler() {
        usersDownloading = new HashMap<>();
        startCleanUpService();
    }

    private void startCleanUpService() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            Nirubot.info("Cleaning TMP Directory!");
            boolean isRunning = true;
            while (isRunning) {
                if (usersDownloading.isEmpty()) {
                    isRunning = false;
                    try {
                        Nirubot.deleteRecursive(TMP_DIR);
                    } catch (IOException e) {
                        Nirubot.warning(e.getMessage());
                    }
                    TMP_DIR.mkdirs();
                }
            }
        }, 1, 1, TimeUnit.DAYS);
    }

    public boolean isUserDownloading(Snowflake id) {
        return this.usersDownloading.containsKey(id);
    }

    public boolean startDownload(CommandContext ctx, List<String> args, User author) {
        if (usersDownloading.containsKey(author.getId())) {
            if (usersDownloading.get(author.getId()).isRunning()) {
                return false;
            } else {
                usersDownloading.remove(author.getId());
            }
        }

        YoutubeDLThread t = new YoutubeDLThread(ctx, args, author);

        usersDownloading.put(author.getId(), t);
        t.start();
        return true;
    }
}
