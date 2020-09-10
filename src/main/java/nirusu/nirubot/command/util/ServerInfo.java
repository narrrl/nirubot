package nirusu.nirubot.command.util;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;

public class ServerInfo implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() != 1) {
            return;
        }

        Guild g = ctx.getGuild();

        EmbedBuilder emb = new EmbedBuilder();

        emb.setTitle(g.getName())
                .setDescription("Name: " + g.getName() + "\nCreation Date: "
                        + g.getTimeCreated().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm"))
                        + "\nMember Count: " + g.getMemberCount() + "\nRegion: " + g.getRegionRaw() + "\nOwner: "
                        + g.getOwner().getAsMention())
                .setThumbnail(g.getIconUrl()).setColor(Nirubot.getColor());
        ctx.reply(emb.build());

    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Lists some usefull info about the server", gm.prefix(), this);
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("server");
    }

    
}
