package nirusu.nirubot.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.google.gson.annotations.SerializedName;

import nirusu.nirubot.Nirubot;

/**
 * This class handles all the configurations for the bot. The configurations get
 * loaded on startup and writes all changes to the config.json. The json gets
 * readed with {@link Gson}.
 */
public class Config {
    private final File configFile;
    private final Data data;

    /**
     * Stores all the attributes for the config.json. Is needed to parse with
     * {@link Gson}
     */
    public static class Data {
        private String prefix;
        private String activity;
        private String activityType;
        private String token;
        private String googleApiToken;
        private String host;
        private String tmpDirPath;
        private long[] owners;
        @SerializedName("ts3-channel")
        private String ts3ChannelName;
        @SerializedName("ts3-login")
        private String ts3ServerLogin;
        @SerializedName("ts3-password")
        private String ts3ServerPassword;
        @SerializedName("ts3-ip")
        private String ts3Ip;
    }

    public Config() throws IOException {
        // reades config file from root dir
        configFile = new File(System.getProperty("user.dir").concat(File.separator + "config.json"));
        // convert to data object with gson
        data = Nirubot.getGson().fromJson(Files.readString(configFile.toPath(), StandardCharsets.UTF_8), Data.class);
    }

    /**
     * Writes the {@link #data} to the config file with {@link Gson}. If the config
     * couldn't be written the bot prints a warning message but will continue as
     * nothing happened. That means the changed settings won't be saved after a bot
     * restart and have to be setted again.
     */
    private synchronized void write() {
        try (FileWriter writer = new FileWriter(configFile)) {
            String json = Nirubot.getGson().toJson(data);
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            Nirubot.warning("Couldn't write to config file");
        }

    }

    public synchronized void setActivity(final String newActivity) {
        data.activity = newActivity;
        write();
    }

    public synchronized void setPrefix(final String newPrefix) {
        data.prefix = newPrefix;
        write();
    }

    public synchronized void setActivityType(final String newType) {
        data.activityType = newType;
        write();
    }

    public String getTS3Channel() {
        return data.ts3ChannelName;
    }

    public String getTS3Login() {
        return data.ts3ServerLogin;
    }

    public String getTS3Password() {
        return data.ts3ServerPassword;
    }

    public String getTS3Ip() {
        return data.ts3Ip;
    }

    public String getToken() {
        return data.token;
    }

    public String getYouTubeKey() {
        return data.googleApiToken;
    }

    public long[] getOwners() {
        return data.owners;
    }

    public synchronized String getActivity() {
        return data.activity;
    }

    public synchronized String getActivityType() {
        return data.activityType;
    }

    public synchronized String getPrefix() {

        // gson parses prefix as null if its not set in the config.json
        if (data.prefix == null) {
            throw new IllegalArgumentException("Set prefix in config.json!");
        }

        return data.prefix;
    }

    public String getTmpDirPath() {
        if (data.tmpDirPath == null)
            throw new IllegalArgumentException("No temp dir set in config!");
        return this.data.tmpDirPath;
    }

    public String getHost() {
        if (data.host == null)
            throw new IllegalArgumentException("No hostname declared in config!");
        return this.data.host;
    }

	public synchronized void setOwner(long id) {
        long[] newArr = new long[data.owners.length + 1];
        System.arraycopy(data.owners, 0, newArr, 1, data.owners.length);
        newArr[0] = id;
        data.owners = newArr;
        write();
	}

}
