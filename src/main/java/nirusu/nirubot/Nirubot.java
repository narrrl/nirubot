package nirusu.nirubot;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nirusu.nirubot.core.Config;
import nirusu.nirubot.listener.DiscordListener;
import nirusu.nirubot.listener.GameRequestListener;
import nirusu.nirubot.listener.NiruListener;

public class Nirubot extends AbstractIdleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Nirubot.class);
    public static int EXIT_CODE_ERROR = -1;
    public static int EXIT_CODE_SUCCESS = 0;
    private static Nirubot bot;
    private static Gson gson;
    private static Config conf;
    private static YouTube yt;
    private static File tmpDir;
    private static File tmpWebDir;
    private ArrayList<NiruListener> listeners;

    public static Nirubot getNirubot() {
        if (bot == null) {
            bot = new Nirubot();
        }
        return bot;
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static Config getConfig() {
        if (conf == null) {
            try {
                conf = new Config();
            } catch (IOException e) {
                getLogger().warn(e.getMessage());
            }
        }
        return conf;
    }

    public static File getTmpDirectory() {

        if (tmpDir == null) {
            tmpDir = new File(System.getProperty("user.dir").concat(File.separator).concat("tmp"));
            tmpDir.mkdir();
        }

        return tmpDir;
    }

    public static File getWebDir() {

        if (tmpWebDir == null) {
            tmpWebDir = new File("/var/www/html/discord/tmp");
        }

        return tmpWebDir;
    }

    public Nirubot() {
        super();
        listeners = new ArrayList<>();

    }


    public static void main(String[] args) {
        var bot = getNirubot();
        bot.addListener(new Listener() {
            @Override
            public void starting() {
                info("Nirubot is starting");
            }

            @Override
            public void running() {
                info("Nirubot is now running");
            }

            @Override
            public void stopping(State from) {
                info("Nirubot is stopping from {}", from);
            }

            @Override
            public void terminated(State from) {
                info("Nirubot has terminated (was {})", from);
                System.exit(EXIT_CODE_SUCCESS);

            }

            @Override
            public void failed(State from, Throwable failure) {
                error("Nirubot couldn't start due to a critical error during {} from and will now terminate", from, failure);
                System.exit(EXIT_CODE_ERROR);
            }

        }, executor -> new Thread(executor, "Watchdog").start());
        bot.startAsync();
    }

    public static Logger getLogger() {
        return LOGGER;
    }


    // some print methods for the log
    public static void info(final String message) {
        LOGGER.info(message);
    }

    public static void info(final String message, Object from) {
        LOGGER.info(message, from);
    }

    public static void warning(final String message) {
        LOGGER.warn(message);
    }    
    
    public static void error(final String message, Object from) {
        LOGGER.error(message, from);
    }

    public static void error(final String message, Object from, Object cause) {
        LOGGER.error(message, from, cause);
    }

    @Override
    protected void startUp() throws Exception {
        // command handling etc for discord
        listeners.add(new DiscordListener());
        listeners.add(GameRequestListener.getInstance());
    }

    @Override
    protected void shutDown() throws Exception {
        // shutdown everything
        listeners.forEach(NiruListener::shutdown);
    }

    /**
     * {@link nirusu.nirubot.core.Config#getPrefix()} gets the prefix from the config.json
     * @return prefix that was set in config.json
     */
    public static String getDefaultPrefix() {
        return getConfig().getPrefix();
    }

	public static Color getColor() {
		return new Color(0, 153, 255);
	}

	public synchronized void shutdown() {
        try {
            stopAsync();
        } catch (Exception e) {
            Nirubot.error("Couldn't shutdown bot!", e);
            System.exit(EXIT_CODE_ERROR);
        }
    }

    public static boolean isOwner(long id) {
        for (long l : getConfig().getOwners()) {
            if (id == l) {
                return true;
            }
        }
        return false;
    }

    public static synchronized YouTube getYouTube() {
        if (yt == null) {
            yt = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer(){
				@Override
				public void initialize(HttpRequest request) throws IOException {
				}
            }).setApplicationName("Nirubot").build();
        }
        return yt;
    }

	public static String getHost() {
		return getConfig().getHost();
	}

	public static String getTmpDirPath() {
		return getConfig().getTmpDirPath();
	}
}
