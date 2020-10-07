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
import nirusu.nirubot.core.GuildMusicManager;
import nirusu.nirubot.core.PlayerManager;
import nirusu.nirubot.listener.GameRequestListener.RequestCMD;

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
        CommandDispatcher.checkForDuplicateAlias();
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
            // created new CommandContext with arguments as list (seperated by whitespaces)
            CommandContext ctx = new CommandContext(event, Arrays.asList(content.split("\\s+")));

            RequestCMD rcmd = RequestCMD.getRequestCMD(ctx.getArgs().get(0));
            // check for invalid command
            if (!rcmd.equals(RequestCMD.INVALID)) {
                try {
                    // run command
                    rcmd.run(ctx.getMessage().getMentionedUsers(), ctx.getChannel(), ctx.getAuthor());
                    return;
                } catch (IllegalArgumentException e) {
                    // inform user about error
                    ctx.reply(e.getMessage());
                    return;
                }
            }

            ICommand cmd;

            try {
                cmd = CommandDispatcher.getICommand(ctx.getArgs().get(0));
            } catch (IllegalArgumentException e) {
                ctx.reply("Command not found!");
                return;
            }

            cmd.execute(ctx);
        }
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        Nirubot.info("{} ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
	public void shutdown() {
        for (GuildMusicManager m : PlayerManager.getInstance().getAllManager()) {
            m.getPlayer().destroy();
        }
        for (JDA j : shardManager.getShards()) {
            j.shutdown();
        }
        shardManager.shutdown();
        Nirubot.info("Discord listener is shutting down");
	}
    
}
