package nirusu.nirubot.service.discord;

import java.util.Optional;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirucmd.CommandContext;
import nirusu.nirucmd.CommandToRun;
import reactor.core.publisher.Mono;

public class MessageCreateListener implements EventListener<MessageCreateEvent> {

    @Override
    public Class<MessageCreateEvent> eventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        Message mes = event.getMessage();

        Optional<User> auth = mes.getAuthor();

        if (auth.map(User::isBot).orElse(true)) {
            return Mono.empty();
        }

        // get message content
        String raw = mes.getContent();

        CommandContext ctx = new CommandContext(event);

        String prefix;

        if (ctx.isGuild()) {
            prefix = ctx.getGuild().map(g -> GuildManager.of(g.getId()).prefix()).orElse(Nirubot.getDefaultPrefix());
        } else if (ctx.isPrivate()) {
            prefix = "";
        } else {
            prefix = Nirubot.getDefaultPrefix();
        }

        // check if message starts with prefix !
        if (raw.startsWith(prefix) && raw.length() > prefix.length()) {
            // create the CommandContext
            String[] userInput = raw.substring(prefix.length()).split("\\s+");
            ctx.setArgsAndKey(userInput, userInput[0], true);
            CommandToRun cmd = Nirubot.getNirubot().getDispatcher().getCommand(ctx, ctx.getKey());
            cmd.run();
        } else if (ctx.getSelf().map(self -> mes.getUserMentionIds().contains(self.getId())).orElse(false)) {
            ctx.reply(String.format("The prefix is %s", prefix));
        }
        return Mono.empty();
    }
    
}
