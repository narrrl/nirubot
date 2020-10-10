package nirusu.nirubot.listener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.util.GameRequestManager;

/**
 * This class checks for GameRequest-Events every minute.
 */
public class GameRequestListener implements NiruListener {

    public enum RequestCMD {
        YES {
            @Override
            public void run(List<User> users, TextChannel ch, User user) {
                if (users.size() != 1) {
                    throw new IllegalArgumentException("You have to mention the author of the request!");
                }

                GameRequestListener.getInstance().setUser(ch, user, true, users.get(0));
            }
        },
        NO {
            @Override
            public void run(List<User> users, TextChannel ch, User user) {
                if (users.size() != 1) {
                    throw new IllegalArgumentException("You have to mention the author of the request!");
                }

                GameRequestListener.getInstance().setUser(ch, user, false, users.get(0));
            }
        },
        STATUS {
            @Override
            public void run(List<User> users, TextChannel ch, User user) {
                if (users.size() != 1) {
                    throw new IllegalArgumentException("You have to mention the author of the request!");
                }
                GameRequestManager mg = GameRequestListener.getInstance().getManager(ch, users.get(0));
                mg.getChannel().sendMessage(mg.toEmb()).queue();
            }
        },
        CANCEL {
            @Override
            public void run(List<User> users, TextChannel ch, User user) {
                if (users.size() != 0) {
                    throw new IllegalArgumentException("Invalid arguments");
                }

                GameRequestListener.getInstance().cancel(ch, user);
            }
        },
        // null object
        INVALID {
            @Override
            public void run(List<User> users, TextChannel ch, User user) {
            }
        };

        public abstract void run(List<User> users, TextChannel ch, User user);

        public static RequestCMD getRequestCMD(final String cmd) {
            for (RequestCMD c : RequestCMD.values()) {
                if (cmd.toUpperCase().equals(c.name())) {
                    return c;
                }
            }
            return RequestCMD.INVALID;
        }
    }

    class EventThread extends Thread {

        private boolean isRunning;

        EventThread() {
            super();
            isRunning = true;
        }

        @Override
        public void run() {
            while (isRunning) {
                Date d = Calendar.getInstance().getTime();
                // copy managers
                List<GameRequestManager> ls = getManagers();
                for (GameRequestManager m : ls) {
                    if (m.timeReached(d)) {
                        // send notification
                        m.send();
                        // remove from the managers list
                        managers.remove(m);
                    }
                }
                // sleep 60s
                try {
                    sleep(60000);
                } catch (InterruptedException e) {
                    Nirubot.warning(e.getMessage());
                }
            }
        }

        public void shutdown() {
            this.isRunning = false;
        }

    }

    private static GameRequestListener listener;
    private ArrayList<GameRequestManager> managers;
    private EventThread t;

    public static synchronized GameRequestListener getInstance() {
        if (listener == null) {
            listener = new GameRequestListener();
        }
        return listener;
    }

    private GameRequestListener() {
        managers = new ArrayList<>();

        // create thread to check every minute if a request timelimit is reached


        t = new EventThread();

        t.start();
    }

    private synchronized List<GameRequestManager> getManagers() {
        return managers.stream().collect(Collectors.toList());
    }

    public void addManager(final GameRequestManager mg) {
        // check if user already added a request in this channel
        if (managers.contains(mg)) {
            throw new IllegalArgumentException("You can't create two request in one channel");
        }
        managers.add(mg);
    }

	public GameRequestManager setUser(TextChannel channel, User user, boolean b, User author) {
        // cant be null
        GameRequestManager m = getManager(channel, author);
        // if user is not in the request nothing happens
        m.setUser(b, user);
        channel.sendMessage(m.toEmb()).queue();;
        return m;
	}

	public GameRequestManager getManager(TextChannel channel, User author) {
        for (GameRequestManager m : managers) {
            // identified by author and channel of the request
            if (channel.equals(m.getChannel()) && m.getAuthor().getIdLong() == author.getIdLong()) {
                return m;
            }
        }
        throw new IllegalArgumentException("No GameRequest found");
	}

	public GameRequestManager cancel(TextChannel channel, User author) {
        // cant be null
        GameRequestManager rq = getManager(channel, author);
        managers.remove(rq);
        channel.sendMessage("GameRequest deleted").queue();;
        return rq;
	}

    @Override
    public void shutdown() {
        t.shutdown();
        Nirubot.info("GameRequest is shutting down");
    }
}
