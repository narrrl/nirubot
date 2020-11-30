package nirusu.nirubot.command;

import discord4j.core.object.entity.Message;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

public class HelpModule extends BaseModule {

    @Command( 
        key = "ping", 
        description = "This command pings the bot")
    public void ping() {
        long curr = System.currentTimeMillis();
        Message mes = ctx.reply("Pong!");
        long ping = mes.getTimestamp().toEpochMilli() - curr;
        // not very accurate ¯\_(ツ)_/¯
        mes.edit(edit -> edit.setContent(String.format("Pong: %d ms", ping))).block();
    }
}
