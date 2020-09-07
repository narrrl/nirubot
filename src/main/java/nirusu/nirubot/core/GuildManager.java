package nirusu.nirubot.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;

import nirusu.nirubot.Nirubot;

/**
 * This class handles all the guild specific settings which are stored in {@link Guild}. The attributes for {@link Guild}
 * gets written into a json with the guild long id as name.
 * 
 */
public class GuildManager {

    public class Guild {
        private final long id;
        private boolean successReaction;
        private int volume;
        private String prefix;

        public Guild(long longId) {
            this.id = longId;
        }

    }

    private static final File GUILDS_DIR = new File(System.getProperty("user.dir").concat(File.separator + "guilds"));
    private static HashMap<Long, GuildManager> guildManagers;
    private Guild guild;
    private File guildFile;

    private static HashMap<Long, GuildManager> getGuildManagers() {

        if (guildManagers == null) {
            guildManagers = new HashMap<>();
        }

        return guildManagers;
    }

    public static GuildManager getManager(long idLong) {
        GuildManager gm = getGuildManagers().get(idLong);

        if (gm == null) {
            try {
                gm = new GuildManager(idLong);
            } catch (IOException e) {
                Nirubot.warning("couldn't create config for guild" + idLong);
            }
            getGuildManagers().put(idLong, gm);
        }

        return gm;
    }

    GuildManager(long longId) throws IOException {

        if (!GUILDS_DIR.exists()) {
            GUILDS_DIR.mkdir();
        }

        guildFile = new File(GUILDS_DIR.getAbsolutePath().concat(File.separator + longId + ".json"));
        if (!guildFile.exists() && guildFile.createNewFile()) {
            guild = new Guild(longId);
            guild.prefix = Nirubot.getDefaultPrefix();
            guild.volume = 100;
            guild.successReaction = false;
            write();
        } else if (!guildFile.exists()) {
            throw new IllegalArgumentException("Couldn't read or create guild config for " + longId);
        }
        guild = Nirubot.getGson().fromJson(Files.readString(guildFile.toPath(), StandardCharsets.UTF_8), Guild.class);

        if (guild == null) {
            guild = new Guild(longId);
            guild.prefix = Nirubot.getDefaultPrefix();
            guild.successReaction = false;
            guild.volume = 100;
            write();
        }
    }

    private synchronized void write() {
        FileWriter writer;
        try {
            writer = new FileWriter(guildFile);
            String json = Nirubot.getGson().toJson(guild, Guild.class);
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Nirubot.warning(e.getMessage());
        }
    }

    public synchronized void setPrefix(final String prefix) {
        guild.prefix = prefix;
        write();
    }

    public synchronized void setSuccessReaction(final boolean b) {
        guild.successReaction = b;
        write();
    }

    public synchronized void setVolume(final int volume) {
        guild.volume = volume;
        write();
    }

    public String prefix() {
        if (guild.prefix == null) {
            setPrefix(Nirubot.getDefaultPrefix());
            write();
        }
        return guild.prefix;
    }

    public int volume() {
        return guild.volume;
    }

    public long id() {
        return guild.id;
    }

    public boolean successReaction() {
        return guild.successReaction;
    }

}
