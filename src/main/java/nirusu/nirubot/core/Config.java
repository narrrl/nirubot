package nirusu.nirubot.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import nirusu.nirubot.Nirubot;

public class Config {
    private final File configFile;
    private final Data data;

    public class Data {
        private String prefix;
        private String activity;
        private String activityType;
        private String token;

    }

    public Config() throws IOException {
        configFile = new File(System.getProperty("user.dir").concat(File.separator + "config.json"));
        data = Nirubot.getGson().fromJson(Files.readString(configFile.toPath(), StandardCharsets.UTF_8), Data.class);
    }

    public String getActivityType() {
        return data.activityType;
    }

    public void setActivityType(final String newType) {
        data.activityType = newType;
    }

    public String getPrefix() {
        if (data.prefix == null) {
            throw new IllegalArgumentException("Set prefix in config.json!");
        }
        return data.prefix;
    }

    public void setPrefix(final String newPrefix) {
        data.prefix = newPrefix;
    }

    public String getActivity() {
        return data.activity;
    }

    public void setActivity(final String newActivity) {
        data.activity = newActivity;
    }

    public String getToken() {
        return data.token;
    }

}
