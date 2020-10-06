package nirusu.nirubot.command.fun.music;

import java.util.Arrays;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.GuildMusicManager;
import nirusu.nirubot.core.PlayerManager;

public final class Playing implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() != 1) {
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();
        GuildMusicManager musicManager = manager.getGuildMusicManager(ctx.getGuild());
        final AudioTrack track = manager.getPlaying(musicManager);

        if (track == null) {
            ctx.reply("No music is playing!");
            return;
        }

        EmbedBuilder emb = new EmbedBuilder();
        AudioTrackInfo info = track.getInfo();
        String uri = info.uri.startsWith("https://www.youtube.com/watch?v=") 
            ? "https://youtu.be/" + info.identifier +  "?t=" + (track.getPosition() / 1000) : info.uri;
        StringBuilder progress = new StringBuilder();
        float percent = track.getPosition() / (float) info.length;
        int totalTiles = 10;
        int tilesPlayed = (int) (totalTiles * percent);
        tilesPlayed = tilesPlayed == 0 ? 1 : tilesPlayed;
        long minutes = track.getPosition() / 1000 / 60;
        long seconds = track.getPosition() / 1000 % 60;
        progress.append("◄");
        for (int i = 0; i < totalTiles; i++) {
            if (i == tilesPlayed - 1) {
                progress.append("🔘");
            } else {
                progress.append("▬");
            }
        }
        progress.append("►\n " + formatTime(minutes, seconds)+ " / ");
        minutes = track.getDuration() / 1000 / 60;
        seconds = track.getDuration() / 1000 % 60;
        progress.append(formatTime(minutes, seconds));
        emb.setColor(Nirubot.getColor()).setThumbnail(ctx.getGuild().getIconUrl()).setTitle("Now playing:")
                .setDescription("[" + info.title + "]" + "(" + uri + ")\n" + progress.toString());
        ctx.reply(emb.build());

    }

    private String formatTime(final long minutes , final long seconds) {
        StringBuilder out = new StringBuilder();

        if (minutes > 60) {

            long hours = minutes / 60;

            if (hours < 10) {
                out.append("0" + hours);
            } else {
                out.append(hours);
            }

            out.append(":");

            long min = minutes % 60;

            if (min < 10) {
                out.append("0" + min);
            } else {
                out.append(min);
            }
        } else {
            if (minutes == 0) {
                out.append("0");
            }
            else if (minutes < 10) {
                out.append("0" + minutes);
            } else {
                out.append(minutes);
            }
        }

        out.append(":");

        if (seconds < 10) {
            out.append("0" + seconds);
        } else {
            out.append(seconds);
        }

        return out.toString();
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Shows the current playing song", gm.prefix(), this);
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("np");
    }
}
