package nirusu.nirubot.command;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.rest.util.Color;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.audio.GuildMusicManager;
import nirusu.nirubot.core.audio.PlayerManager;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

public class MusicModule extends BaseModule {

    @Command( key = { "p", "play", "pl"}, description = "Plays a song", context = {Command.Context.GUILD})
    public void play() {
        Guild guild = ctx.getGuild().orElseThrow();
        List<String> args = ctx.getArgs().orElseThrow();

        if (!ctx.argsHasLength(1)) {
            return;
        }

        String link = args.get(0);
        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
        VoiceState state = ctx.getAuthorVoiceState().orElse(null);

        if (state == null) {
            ctx.reply("Join a voice channel first!");
            return;
        }

        VoiceChannel ch = state.getChannel().block();
        ch.join(con -> con.setProvider(musicManager.getProvider())).block();
        try {
            PlayerManager.getInstance().loadAndPlay(ctx, link);
        } catch (IllegalArgumentException e) {
            ctx.reply(e.getMessage());
            return;
        }
        ctx.reply("Loaded song!");
    }

    @Command( key = { "skip", "next", "s", "sk"}, description = "Skips the current song", context = {Command.Context.GUILD})
    public void skip() {
        Guild guild = ctx.getGuild().orElseThrow();

        if (!ctx.argsHasLength(0)) {
            return;
        }

        if (!isInSameChannel()) {
            ctx.reply("You must be in the same channel!");
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);

        AudioTrack prev = musicManager.getPlayer().getPlayingTrack();

        if (prev == null) {
            ctx.reply("No music is playing!");
            return;
        }

        PlayerManager.getInstance().next(guild);

        AudioTrack next = musicManager.getPlayer().getPlayingTrack();
        String prevText = "[" + prev.getInfo().title + "](" + prev.getInfo().uri + ")";
        String nextText = next != null ? "[" + next.getInfo().title + "](" + next.getInfo().uri + ")" : "End of queue!";
        ctx.getChannel().createEmbed(spec ->
            spec.setColor(Color.of(Nirubot.getColor().getRGB()))
            .setTitle("Skipped Song!")
            .addField("Skipped:", prevText, true)
            .addField("Next:", nextText, true)
            ).block();
    }

    @Command(key = {"vol", "vl", "volume"}, description = "Sets the volume for the bot")
    public void volume() {
        Guild guild = ctx.getGuild().orElseThrow();
        List<String> args = ctx.getArgs().orElseThrow();

        if (!ctx.argsHasLength(1)) {
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
        int volume;
        try {
            volume = Integer.parseInt(args.get(0));
        } catch ( NumberFormatException e) {
            ctx.reply(String.format("%s is not a valid volume", args.get(0)));
            return;
        }

        if (volume < 0) return;

        volume = volume > 100 ? 100 : volume;

        GuildManager.getManager(guild.getId().asLong()).setVolume(volume);
        musicManager.setVolume(volume);

        int volBars = volume / 10;
        StringBuilder out = new StringBuilder();
        out.append("Volume:\n►");
        for (int i = 0; i < 10; i++) {
            if (i < volBars) {
                out.append("█");
            } else {
                out.append("░");
            }
        }
        out.append("◄\n" + GuildManager.getManager(ctx.getGuild().orElseThrow().getId().asLong()).volume() + "%");

        ctx.getChannel().createEmbed(spec ->
            spec.setColor(Color.of(Nirubot.getColor().getRGB()))
            .setDescription(out.toString())
        ).block();
    }

    @Command(key = {"join", "j"}, description = "Joins into the channel of the author", context = {Command.Context.GUILD})
    public void join() {
        Guild guild = ctx.getGuild().orElseThrow();
        User user = ctx.getAuthor().orElseThrow();

        if (!ctx.argsHasLength(0)) {
            return;
        }
        
        Member member = guild.getMemberById(user.getId()).block();

        VoiceState state = member.getVoiceState().block();

        if (state == null) {
            ctx.reply("Join a voice channel first!");
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
        VoiceChannel ch = state.getChannel().block();
        ch.join(con -> con.setProvider(musicManager.getProvider())).block();

    }

    @Command(key = {"leave", "left", "l"}, description = "Leaves the current voice channel", context = {Command.Context.GUILD})
    public void leave() {
        Guild guild = ctx.getGuild().orElseThrow();

        if (!ctx.argsHasLength(0)) {
            return;
        }

        if (!isBotConnected()) {
            ctx.reply("Bot is not connected to any voice channel!");
            return;
        }
        
        if (!isInSameChannel()) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }
        
        ctx.getSelfVoiceState().orElseThrow().getChannel().block().sendDisconnectVoiceState().block();
        PlayerManager.getInstance().destroy(guild.getId().asLong());
    }

    @Command(key = {"repeat","loop","rp"}, description = "Repeats the current playlist", context = {Command.Context.GUILD})
    public void repeat() {
        if (!ctx.argsHasLength(0)) {
            return;
        }

        if (!isInSameChannel()) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }

        if (PlayerManager.getInstance().repeat(ctx.getGuild().orElseThrow())) {
            ctx.reply("Now repeating current playlist!");
        } else {
            ctx.reply("Stopped repeating playlist!");
        }
        
    }


    private boolean isInSameChannel() {
        if (ctx.getAuthorVoiceState().isEmpty()) {
            return false;
        }
        VoiceState state = ctx.getAuthorVoiceState().get();

        if (ctx.getSelf().isEmpty()) {
            return false;
        }
        return state.getChannel().block().isMemberConnected(ctx.getSelf().get().getId()).block();
    }

    private boolean isBotConnected() {
        return ctx.getSelfVoiceState().isPresent();
    }

    
}
