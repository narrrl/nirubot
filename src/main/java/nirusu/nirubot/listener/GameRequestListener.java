package nirusu.nirubot.listener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import nirusu.nirubot.util.GameRequestManager;

public class GameRequestListener {

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
        INVALID {
            @Override
            public void run(List<User> users, TextChannel ch, User user) {
                throw new IllegalAccessError("The fuck?");
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

    private static GameRequestListener listener;
    private ArrayList<GameRequestManager> managers;

    public static GameRequestListener getInstance() {
        if (listener == null) {
            listener = new GameRequestListener();
        }

        return listener;
    }

    private GameRequestListener() {
        managers = new ArrayList<>();

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Date d = Calendar.getInstance().getTime();
                        ArrayList<GameRequestManager> ls = new ArrayList<>();
                        for (GameRequestManager m : managers) {
                            if (m.timeReached(d)) {
                                m.send();
                                ls.add(m);
                            }
                        }
                        ls.forEach(managers::remove);
                        sleep(60000);
                    }
                } catch (InterruptedException e) {
                    run();
                }
            }
        };
        t.start();
    }

    public void addManager(final GameRequestManager mg) {
        if (managers.contains(mg)) {
            throw new IllegalArgumentException("You can't create two request in one channel");
        }
        managers.add(mg);
    }

	public GameRequestManager setUser(TextChannel channel, User user, boolean b, User author) {
        GameRequestManager m = getManager(channel, author);
        m.setUser(b, user);
        channel.sendMessage(m.toEmb()).queue();;
        return m;
	}

	public GameRequestManager getManager(TextChannel channel, User author) {
        for (GameRequestManager m : managers) {
            if (channel.equals(m.getChannel()) && m.getAuthor().getIdLong() == author.getIdLong()) {
                return m;
            }
        }
        throw new IllegalArgumentException("No GameRequest found");
	}

	public GameRequestManager cancel(TextChannel channel, User author) {
        GameRequestManager rq = getManager(channel, author);
        managers.remove(rq);
        channel.sendMessage("GameRequest deleted").queue();;
        return rq;
	}
}
