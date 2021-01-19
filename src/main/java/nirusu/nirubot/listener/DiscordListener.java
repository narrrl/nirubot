package nirusu.nirubot.listener;

import java.util.Objects;
import java.util.Optional;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.shard.ShardingStrategy;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.Config;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirucmd.CommandContext;
import nirusu.nirucmd.CommandToRun;

public class DiscordListener implements NiruListener {
    private final GatewayDiscordClient client;

    public DiscordListener() {
        Config conf = Nirubot.getConfig();
        client = Objects.requireNonNull(DiscordClient.create(conf.getToken()).gateway()
                .setSharding(ShardingStrategy.recommended()).login().block());

        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> Nirubot.info("Logged in as {}", event.getSelf().getUsername()));

        client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(this::onMessageRecievedEvent);
        client.onDisconnect().block();

    }

    public void onMessageRecievedEvent(MessageCreateEvent event) {
        Message mes = event.getMessage();

        Optional<User> auth = mes.getAuthor();

        if (auth.map(User::isBot).orElse(true)) {
            return;
        }

        // get message content
        String raw = mes.getContent();

        CommandContext ctx = new CommandContext(event);

        String prefix;

        if (ctx.isGuild()) {
            prefix = event.getGuild().blockOptional().map(g -> {
                GuildManager mg = GuildManager.of(g.getId());
                return mg.prefix();
            }).orElse(Nirubot.getDefaultPrefix());
        } else if (ctx.isPrivate()) {
            prefix = "";
        } else {
            prefix = Nirubot.getDefaultPrefix();
        }

        // check if message starts with prefix !
        if (raw.startsWith(prefix) && raw.length() > prefix.length()) {
            // create the CommandContext
            String[] userInput = raw.substring(prefix.length()).split("\\s+");
            ctx.setArgsAndKey(userInput,
                    userInput[0], true);
            CommandToRun cmd = Nirubot.getNirubot().getDispatcher().getCommand(ctx, ctx.getKey());
            cmd.run();
        } else if (ctx.getSelf().map(self -> mes.getUserMentionIds().contains(self.getId())).orElse(false)) {
            ctx.reply(String.format("The prefix is %s", prefix));
        }
    }

    @Override
    public void shutdown() {
        client.logout().blockOptional();
        Nirubot.info("Discord listener is shutting down");
    }
}
