package nirusu.nirubot.command;

import discord4j.rest.util.Permission;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

public class AdminModule extends BaseModule {

    @Command( key = {"shutdown", "shutd"}, description = "Shutdown the bot")
    public void shutdown() {

        ctx.getAuthor().ifPresent(user -> {
            if (Nirubot.isOwner(user.getId().asLong())) {
                ctx.reply("Bai bai!");
                Nirubot.getNirubot().exit();
            }
        });
    }

    @Command( key = {"pref", "prefix"}, description = "Sets the perfix of the bot for a guild or global")
    public void prefix() {
        ctx.getArgs().ifPresent(args -> ctx.getAuthor().ifPresent(user -> {
            if (args.size() != 1) {
                return;
            }
            if (ctx.isPrivate() && Nirubot.isOwner(user.getId().asLong())) {
                Nirubot.getConfig().setPrefix(args.get(0));
            } else if (ctx.hasGuildPermission(Permission.ADMINISTRATOR)) {
                ctx.getGuild().ifPresent(guild -> 
                    GuildManager.of(guild.getId()).setPrefix(args.get(0))
                );
            } else {
                ctx.reply("Not enough permissions!");
                return;
            }
            ctx.reply(String.format("Changed prefix to %s", args.get(0)));
        }));
    }
    
}
