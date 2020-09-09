package nirusu.nirubot.command.fun.music;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.PlayerManager;

public final class Play implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() != 2) {
            return;
        }

        VoiceChannel channel = DiscordUtil.findVoiceChannel(ctx.getSelfMember());

        if (channel != null && !DiscordUtil.areInSameVoice(ctx.getMember(), ctx.getSelfMember())) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();
        manager.loadAndPlay(ctx, ctx.getArgs().get(0));

        GuildManager gm = GuildManager.getManager(ctx.getGuild().getIdLong());

        manager.getGuildMusicManager(ctx.getGuild()).getPlayer()
                .setVolume(gm.volume());

        ctx.getGuild().getAudioManager().openAudioConnection(DiscordUtil.findVoiceChannel(ctx.getMember()));
    }

    @Override
    public String getKey() {
        return "play";
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Connects the bot to your channel and starts playing the song", gm.prefix(), getKey());
    }
}
