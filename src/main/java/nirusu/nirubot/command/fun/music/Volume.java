package nirusu.nirubot.command.fun.music;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.GuildMusicManager;
import nirusu.nirubot.core.PlayerManager;

public final class Volume implements ICommand {

    @Override
    public void execute(final CommandContext ctx) {

        if (ctx.getArgs().size() != 2) {
            return;
        }

        if (DiscordUtil.findVoiceChannel(ctx.getSelfMember()) == null) {
            ctx.reply("No music is playing");
            return;
        }

        if (!DiscordUtil.areInSameVoice(ctx.getMember(), ctx.getSelfMember())) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();
        GuildMusicManager musicManager = manager.getGuildMusicManager(ctx.getGuild());

        int volume;

        try {
            volume = Integer.parseInt(ctx.getArgs().get(1));
        } catch (NumberFormatException e) {
            return;
        }

        GuildManager.getManager(ctx.getGuild().getIdLong()).setVolume(volume);
        musicManager.getPlayer().setVolume(volume);

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
        out.append("◄\n" + volume + "%");
        EmbedBuilder emb = new EmbedBuilder();
        emb.setColor(Nirubot.getColor()).setDescription(out.toString());
        ctx.reply(emb.build());
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Sets the volume for this guild", gm.prefix(), this);
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("vl");
    }
}
