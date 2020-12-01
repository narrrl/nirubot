package nirusu.nirubot.command;

import java.util.List;

import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.VoiceChannel;
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
        User user = ctx.getAuthor().orElseThrow();

        if (!ctx.argsHasLength(1)) {
            return;
        }

        String link = args.get(0);
        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
        Member member = guild.getMemberById(user.getId()).block();
        VoiceState state = member.getVoiceState().block();

        if (state == null) {
            ctx.reply("Join a voice channel first!");
            return;
        }

        VoiceChannel ch = state.getChannel().block();
        ch.join(con -> con.setProvider(musicManager.getProvider())).block();
        PlayerManager.getInstance().loadAndPlay(ctx, link);
        musicManager.setVolume(GuildManager.getManager(guild.getId().asLong()).volume());
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

        PlayerManager.getInstance().next(guild);
    }

    @Command(key = {"vol", "vl", "volume"}, description = "Sets the volume for the bot")
    public void volume() {
        Guild guild = ctx.getGuild().orElseThrow();
        List<String> args = ctx.getArgs().orElseThrow();

        if (!ctx.argsHasLength(1)) {
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
        try {
            musicManager.setVolume(Integer.parseInt(args.get(0)));
        } catch ( NumberFormatException e) {
            ctx.reply(String.format("%s is not a valid volume", args.get(0)));
        }
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
