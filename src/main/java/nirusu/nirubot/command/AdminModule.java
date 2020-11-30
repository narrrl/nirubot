package nirusu.nirubot.command;

import java.util.List;

import discord4j.core.object.entity.User;
import discord4j.rest.util.Permission;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

public class AdminModule extends BaseModule {

    @Command( key = {"shutdown", "shutd"}, description = "Shutdown the bot")
    public void shutdown() {

        if (ctx.getAuthor().isPresent() && !Nirubot.isOwner(ctx.getAuthor().get().getId().asLong())) {
            return;
        }

        ctx.reply("Bai bai!");
        Nirubot.getNirubot().shutdown();
    }

    @Command( key = {"pref", "prefix"}, description = "Sets the perfix of the bot for a guild or global")
    public void prefix() {
        if (!ctx.getAuthor().isPresent() || !ctx.getArgs().isPresent()) {
            return;
        }

        List<String> args = ctx.getArgs().get();
        User u = ctx.getAuthor().get();

        if (args.size() != 1) {
            return;
        }

        if (ctx.isContext(Command.Context.PRIVATE) && Nirubot.isOwner(u.getId().asLong())) {
            Nirubot.getConfig().setPrefix(args.get(0));
        } else if (ctx.hasGuildPermission(Permission.ADMINISTRATOR)) {
            GuildManager.getManager(ctx.getGuild().get().getId().asLong()).setPrefix(args.get(0));
        } else {
            ctx.reply("Not enough permissions!");
        }

        ctx.reply(String.format("Changed prefix to %s", args.get(0)));
    }
    
}
