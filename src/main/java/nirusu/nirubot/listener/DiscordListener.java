package nirusu.nirubot.listener;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.annotation.Command;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.CommandHandler;
import nirusu.nirubot.core.Config;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.PlayerManager;

public class DiscordListener extends ListenerAdapter implements NiruListener {

    ShardManager shardManager;

    public DiscordListener() throws LoginException, IllegalArgumentException {
        Config conf = Nirubot.getConfig();
        // starts bot
        shardManager = DefaultShardManagerBuilder.createDefault(conf.getToken()).setAutoReconnect(true)
                .setStatus(OnlineStatus.ONLINE).addEventListeners(this)
                .setActivity(DiscordUtil.getActivity(conf.getActivityType(), conf.getActivity())).build();
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (user.isBot()) {
            return;
        }

        // gets GuildManager for this guild (to get guild specific prefix etc...)
        GuildManager gm = GuildManager.getManager(event.getGuild().getIdLong());

        String raw = event.getMessage().getContentRaw();

        if (raw.startsWith(gm.prefix()) && raw.length() > gm.prefix().length()) {
            String content = raw.substring(gm.prefix().length());
            // key for the command
            String key = content.split("\\s+")[0];
            // args seperated by whitespaces
            List<String> args = List.of(content.substring(key.length()).split("\\s+"));
            // created new CommandContext with arguments as list (seperated by whitespaces)
            CommandContext ctx = new CommandContext(new MessageReceivedEvent(event.getJDA(), event.getResponseNumber(), event.getMessage()), Command.Context.GUILD);
            ctx.setArgs(args);
            CommandHandler.invoke(ctx, key);
        }
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        Nirubot.info("{} ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void shutdown() {
        PlayerManager.getInstance().shutdown();
        for (JDA j : shardManager.getShards()) {
            j.shutdown();
        }
        shardManager.shutdown();
        Nirubot.info("Discord listener is shutting down");
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
    }
    
}
