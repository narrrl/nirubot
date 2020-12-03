package nirusu.nirubot.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.security.auth.login.LoginException;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.shard.ShardingStrategy;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.Config;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.audio.PlayerManager;
import nirusu.nirucmd.CommandContext;
import nirusu.nirucmd.CommandDispatcher;
import nirusu.nirucmd.annotation.Command.Context;
import nirusu.nirucmd.exception.NoSuchCommandException;

public class DiscordListener implements NiruListener {
    private final CommandDispatcher dispatcher;
    private final GatewayDiscordClient client;

    public DiscordListener() throws LoginException {
        Config conf = Nirubot.getConfig();
        dispatcher = new CommandDispatcher.Builder()
            .addPackage("nirusu.nirubot.command").build();
        
        client = DiscordClient.create(conf.getToken())
            .gateway()
            .setSharding(ShardingStrategy.recommended())
            .login().block();
        
        client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> 
            Nirubot.info("Logged in as {}", event.getSelf().getUsername()));

        client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(event -> onMessageRecievedEvent(event));
        client.onDisconnect().block();

    }

    public void onMessageRecievedEvent(MessageCreateEvent event) {
        Message mes = event.getMessage();

        Optional<User> auth = mes.getAuthor();

        if (auth.isEmpty() || auth.get().isBot()) {
            return;
        }

        // get message content
        String raw = mes.getContent();

        if (raw == null) {
            return;
        }

        CommandContext ctx = new CommandContext(event);

        String prefix;

        if (ctx.isContext(Context.GUILD)) {
            Guild g = event.getGuild().block();
            GuildManager mg = GuildManager.getManager(g.getId().asLong());
            prefix = mg.prefix();
        } else {
            prefix = Nirubot.getDefaultPrefix();
        }

        // check if message starts with prefix !
        if (raw.startsWith(prefix) && raw.length() > prefix.length()) {
            // create the CommandContext
            List<String> args = new ArrayList<>();
            Collections.addAll(args, raw.substring(prefix.length()).split("\\s+"));
            if (!args.isEmpty()) {
                // get key to trigger command
                String key = args.get(0);
                // remove the key from the arguments
                args.remove(key);
                // set arguments for the command context
                ctx.setArgs(args);
                // run dispatcher
                try {
                    dispatcher.run(ctx, key);
                } catch (NoSuchCommandException e) {
                    ctx.reply("Unknown command!");
                }
            }
        }
    }

    @Override
    public void shutdown() {
        for (long id  : PlayerManager.getInstance().getAllIds()) {
            PlayerManager.getInstance().destroy(id);
        }
        client.logout().block();
        Nirubot.info("Discord listener is shutting down");
    }
}
