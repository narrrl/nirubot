package nirusu.nirubot.model.nekolove;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import nirusu.nirubot.Nirubot;

public final class NekoLove {
    private static final String API_URL = "https://neko-love.xyz/api/v1/";
    private static final String REQUEST_TYPE = "GET";
    private static final int SUCESS_RESPONSE = 200;
    private static final int INVALID_RESPONSE = 404;

    private NekoLove() {
        throw new IllegalAccessError();
    }

    public static NekoLoveImage getNekoLoveImage(String type) {
        StringBuilder json = new StringBuilder();
        int response;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(API_URL.concat(type)).openConnection();
            connection.setRequestMethod(REQUEST_TYPE);
            response = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                json.append(line);
            }
            in.close();
        } catch (IOException e) {
            if (connection != null)
                connection.disconnect();
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        if (response == INVALID_RESPONSE) {
            throw new IllegalArgumentException(String.format("Invalid Type: %s", type));
        }

        if (response != SUCESS_RESPONSE) {
            throw new IllegalArgumentException(String.format("Invalid Response: %d", response));
        }
        return Nirubot.getGson().fromJson(json.toString(), NekoLoveImage.class);
    }

    public static class NekoLoveImage {
        private int code;
        private String url;

        public String url() {
            if (code != 200 && url == null) {
                return "";
            }
            return this.url;
        }

        public int code() {
            return this.code;
        }
    }

    public enum NEKO_TYPE {
        NEKO, NEKOLEWD, KITSUNE, PAT, HUG, WAIFU, CRY, KISS, SLAP, SMUG, PUNCH
    }
}
