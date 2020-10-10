package nirusu.nirubot.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;

public class GameRequestManager {

    class RequestedUser {
        private User user;
        private boolean isDown;

        RequestedUser(final User user) {
            this.user = user;
            this.isDown = false;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!o.getClass().equals(this.getClass())) return false;

            RequestedUser us = (RequestedUser) o;

            return us.user.getIdLong() == this.user.getIdLong();
        }
    }
    private ArrayList<RequestedUser> users;
    private Date date;
    private String game;
    private TextChannel channel;
    private User author;
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");

    public GameRequestManager(List<User> us, final String d, final String g, final TextChannel ch, final User author) {
        this.author = author;
        this.users = new ArrayList<>();
        this.channel = ch;
        us.forEach(u -> this.users.add(new RequestedUser(u)));
        this.users.remove(new RequestedUser(author));
        RequestedUser auth = new RequestedUser(author);
        auth.isDown = true;
        this.users.add(auth);


        this.game = g;

        setTime(d);
    }

    public void setTime(final String d) {
        if (!d.matches("\\d{1,2}:\\d{2}")) {
            throw new IllegalArgumentException("Invalid time format");
        }


        int hours = Integer.parseInt(d.split(":", -1)[0]);
        int minutes = Integer.parseInt(d.split(":", -1)[1]);

        if (hours < 0 || hours > 24 || minutes > 60 || minutes < 0) {
            throw new IllegalArgumentException("Invalid time format");
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, minutes);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        this.date = c.getTime();
    }

    public boolean timeReached(final Date d) {
        if (d.getTime() >= this.date.getTime()) {
            return true;
        }
        return false;
    }

    public void setUser(final boolean isDown, final User user) {
        for(RequestedUser u : users) {
            if (u.user.getIdLong() == user.getIdLong()) {
                u.isDown = isDown;
            }
        }
    }

    public MessageEmbed toEmb() {
        EmbedBuilder emb = new EmbedBuilder();
        StringBuilder us = new StringBuilder();
        this.users.forEach(u -> {
            if (u.isDown) {
                us.append(u.user.getAsMention() + ": " + ":white_check_mark:");
            } else {
                us.append(u.user.getAsMention() + ": " + ":x:");
            }
            us.append("\n");
        });
        String prefix = GuildManager.getManager(channel.getGuild().getIdLong()).prefix();
        emb.setColor(Nirubot.getColor()).setTitle(game + " at " + dateFormat.format(date)).setDescription(us.substring(0, us.length() - 1) 
        + "\nType **" + prefix + "yes " + author.getAsMention() + "** or **" + prefix + "no " + author.getAsMention() 
        + "** to verify\nYou can check the status with **" + prefix + "status " + author.getAsMention() + "**\nIf you want to cancel the request type **" + prefix + "cancel**");
        return emb.build();
    }

    public TextChannel getChannel() {
        return this.channel;
    }

    public User getAuthor() {
        return this.author;
    }

	public void send() {
        StringBuilder b = new StringBuilder();
        users.forEach(u -> {
            if (u.isDown) {
                b.append(u.user.getAsMention());
            }
        });

        channel.sendMessage("It's time for " + game + " " + b.toString()).queue();
	}

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;

        if (o == this) return true;

        if (!o.getClass().equals(this.getClass())) return false;

        GameRequestManager mg = (GameRequestManager) o;

        return this.channel.equals(mg.channel) && this.author.equals(mg.author);
    }
}
