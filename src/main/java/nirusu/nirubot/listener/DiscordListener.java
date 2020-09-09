package nirusu.nirubot.listener;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.CommandDispatcher;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.Config;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;

public class DiscordListener extends ListenerAdapter implements NiruListener {

    ShardManager shardManager;

    public DiscordListener() throws LoginException, IllegalArgumentException {
        Config conf = Nirubot.getConfig();
        // starts bot
        shardManager = DefaultShardManagerBuilder.createDefault(conf.getToken())
            .setAutoReconnect(true)
            .setStatus(OnlineStatus.ONLINE)
            .addEventListeners(this)
            .setActivity(DiscordUtil.getActivity(conf.getActivityType(), conf.getActivity()))
            .build();

    }


    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (user.isBot()) {
            return;
        }

        GuildManager gm = GuildManager.getManager(event.getGuild().getIdLong());

        String raw = event.getMessage().getContentRaw();

        if (raw.startsWith(gm.prefix()) && raw.length() > gm.prefix().length()) {
            String content = raw.substring(gm.prefix().length());
            CommandContext ctx = new CommandContext(event, Arrays.asList(content.split("\\s+")));

            ICommand cmd = CommandDispatcher.getICommand(ctx.getArgs().get(0));

            cmd.execute(ctx);
        }
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        Nirubot.info("{} ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
	public void shutdown() {
        for (JDA j : shardManager.getShards()) {
            j.shutdown();
        }
        shardManager.shutdown();
        Nirubot.info("Discord listener is shutting down");
	}
    
}
