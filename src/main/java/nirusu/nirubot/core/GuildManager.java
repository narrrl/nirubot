package nirusu.nirubot.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;

import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager.Guild.Playlist;

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
        private HashMap<String, Playlist> playlists;

        public Guild(long longId) {
            this.id = longId;
            playlists = new HashMap<>();
        }

        public void addPlaylist(final String name, final String[] playlist) {
            Playlist pl = new Playlist();
            pl.songs = playlist;
            playlists.put(name, pl);
        }



        public class Playlist {
            String[] songs;
        }

    }

    private static final File GUILDS_DIR = new File(System.getProperty("user.dir").concat(File.separator + "guilds"));
    private static HashMap<Long, GuildManager> guildManagers;
    private Guild guild;
    private File guildFile;


    /**
     * Singleton for guild manager hashmap. That stores all guild managers by their long id
     *
     * @return {@link #guildManagers}
     */
    private static HashMap<Long, GuildManager> getGuildManagers() {

        if (guildManagers == null) {
            guildManagers = new HashMap<>();
        }

        return guildManagers;
    }

    /**
     * Tries to get the guild manager for a given guild id. Creates a new guildmanager if it doesnt exists.
     *
     * @param idLong
     * @throws IllegalArgumentException if the config couldn
     * @return
     */
    public static GuildManager getManager(long idLong) {
        GuildManager gm = getGuildManagers().get(idLong);

        if (gm == null) {
            try {
                gm = new GuildManager(idLong);
            } catch (IOException e) {
                Nirubot.warning("couldn't create config for guild" + idLong);
                gm = new GuildManager(idLong, Nirubot.getDefaultPrefix());
            }
            getGuildManagers().put(idLong, gm);
        }

        return gm;
    }

    /**
     * Creates a new guild manager for given id. Writes to a config to save the guild settings in {@link #guild}
     * @param longId
     * @throws IOException
     */
    GuildManager(long longId) throws IOException {

        // creates guilds directory where the configs of all guilds get saved
        if (!GUILDS_DIR.exists()) {
            GUILDS_DIR.mkdir();
        }

        guildFile = new File(GUILDS_DIR.getAbsolutePath().concat(File.separator + longId + ".json"));

        // checks if guild files exists and creates it if not
        if (!guildFile.exists() && guildFile.createNewFile()) {
            guild = new Guild(longId);
            guild.prefix = Nirubot.getDefaultPrefix();
            guild.volume = 100;
            guild.successReaction = false;
            write();
        } else if (!guildFile.exists()) { // if file couldn't be created
            throw new IllegalArgumentException("Couldn't read or create guild config for " + longId);
        }
        // gets the data from the file and parse it
        guild = Nirubot.getGson().fromJson(Files.readString(guildFile.toPath(), StandardCharsets.UTF_8), Guild.class);

        // checks if guild is null to prevent errors
        if (guild == null) {
            guild = new Guild(longId);
            guild.prefix = Nirubot.getDefaultPrefix();
            guild.successReaction = false;
            guild.volume = 100;
            write();
        }
    }

    /**
     * Constructor if the normal guild manager constructor couldn't create config.
     * Guild settings will reset on bot restart
     *
     * @param id guild id
     * @param prefix guild prefix
     */
    private GuildManager(final long id, final String prefix) {
        this.guild = new Guild(id);
        this.guild.prefix = prefix;
        this.guild.volume = 100;
        this.guild.successReaction = false;
    }


    // writes the data stores in {@link #guild}
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

    // wont return null to prevent nullpointer exceptions
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

    public String[] getPlaylist(final String name) {
        Playlist pl = this.guild.playlists.get(name);
        if (pl == null) {
            throw new IllegalArgumentException("Cannot find playlist with name " + name);
        }
        return pl.songs;
    }

    public synchronized void addPlaylist(final String name, final String[] playlist) {
        Playlist pl = this.guild.playlists.get(name);

        if (pl != null) {
            removePlaylist(name);
        }

        this.guild.addPlaylist(name, playlist);
        write();
    }

    public synchronized void removePlaylist(final String name) {
        this.guild.playlists.remove(name);
    }

}
