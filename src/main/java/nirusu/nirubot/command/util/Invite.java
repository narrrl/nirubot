package nirusu.nirubot.command.util;

import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;

public class Invite implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() != 1) {
            return;
        }

        EmbedBuilder emb = new EmbedBuilder();
        emb.setTitle("Invite " + ctx.getSelfUser().getAsTag() + " to your server!",
                String.format("https://discord.com/api/oauth2/authorize?client_id=%d&permissions=8&scope=bot",
                        ctx.getSelfUser().getIdLong()))
                .setImage("https://tenor.com/view/re-zero-memory-snow-emilia-rem-alcohol-gif-17838827").setColor(Nirubot.getColor());
        ctx.reply(emb.build());
    }

    @Override
    public List<String> alias() {
        return Collections.singletonList("inv");
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Sends an invite for the bot", gm.prefix(), this);
    }

}
