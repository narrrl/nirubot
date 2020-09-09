package nirusu.nirubot.command.util;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;

public class Shutdown implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() != 1) {
            return;
        }

        int offset = 0;
        for (long id : Nirubot.getConfig().getOwners()) {
            if (id == ctx.getAuthor().getIdLong()) {
                offset++;
            }
        }

        if (offset == 0) {
            ctx.reply("Not enough permissions");
            return;
        }

        ctx.getChannel().sendMessage("Bot is shutting down!").complete();
        Nirubot.getNirubot().shutdown();

    }

    @Override
    public String getKey() {
        return "shutdown";
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("This command will shutdown the bot", gm.prefix(), getKey());
    }
    
}
