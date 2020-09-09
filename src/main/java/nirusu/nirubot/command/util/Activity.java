package nirusu.nirubot.command.util;

import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;

public class Activity implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (!Nirubot.getNirubot().isOwner(ctx.getAuthor().getIdLong())) {
            ctx.reply("Not enough permissions");
        }

        List<String> args = ctx.getArgs();

        GuildManager gm = GuildManager.getManager(ctx.getGuild().getIdLong());

        if (args.size() < 3) {
            ctx.reply("Too many arguments. Usage: " + gm.prefix() + getKey() + " <listening|playing|watching> <message>");
        }

        StringBuilder message = new StringBuilder();

        for (int i = 2; i < args.size(); i++) {
            message.append(args.get(i)).append(" ");
        }

        try {
            ctx.getShardManager().setActivity(DiscordUtil.getActivity(args.get(1), message.substring(0, message.length() - 1)));
            Nirubot.getConfig().setActivity(message.substring(0, message.length() - 1));
            Nirubot.getConfig().setActivityType(args.get(1));

        } catch (IllegalArgumentException e) {
            ctx.reply("Invalid activity type!");
        }

    }

    @Override
    public String getKey() {
        return "activity";
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("This commands sets the activity for the bot", gm.prefix(), getKey());
    }
    
}
