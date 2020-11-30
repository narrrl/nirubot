package nirusu.nirubot.command;

import java.util.List;

import discord4j.core.object.entity.Guild;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.audio.GuildMusicManager;
import nirusu.nirubot.core.audio.PlayerManager;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

public class MusicModule extends BaseModule {

    @Command( key = { "p", "play", "pl"}, description = "Plays a song", context = {Command.Context.GUILD})
    public void play() {
        Guild guild = ctx.getGuild().orElse(null);
        List<String> args = ctx.getArgs().orElse(null);
        
        if (guild == null || args == null) {
            return;
        }

        if (args.size() != 1) {
            return;
        }

        String link = args.get(0);

        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
        PlayerManager.attachToFirstVoiceChannel(guild, musicManager.getProvider());
        PlayerManager.getInstance().loadAndPlay(ctx, link);
        musicManager.setVolume(GuildManager.getManager(guild.getId().asLong()).volume());
    }

    @Command( key = { "skip", "next", "s", "sk"}, description = "Skips the current song", context = {Command.Context.GUILD})
    public void skip() {
        Guild guild = ctx.getGuild().orElse(null);
        List<String> args = ctx.getArgs().orElse(null);

        if (guild == null || args == null || args.size() != 0) {
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
        PlayerManager.getInstance().next(musicManager);
    }

    @Command(key = {"vol", "vl", "volume"}, description = "Sets the volume for the bot")
    public void volume() {
        Guild guild = ctx.getGuild().orElse(null);
        List<String> args = ctx.getArgs().orElse(null);

        if (guild == null || args == null || args.size() != 1) {
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
        try {
            musicManager.setVolume(Integer.parseInt(args.get(0)));
        } catch ( NumberFormatException e) {
            ctx.reply(String.format("%s is not a valid volume", args.get(0)));
        }
    }
    
}
