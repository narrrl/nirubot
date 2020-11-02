package nirusu.nirubot.command.module;

import nirusu.nirubot.annotation.Command;
import nirusu.nirubot.command.BaseModule;

public class UtilModule extends BaseModule {

    @Command(
        key = "ping",
        description = "Pings the bot",
        contexts = { Command.Context.GUILD, Command.Context.PRIVATE }
    )
    void ping() {
        ctx.reply("Pong!");
    }
    
}
