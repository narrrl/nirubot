package nirusu.nirubot.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * Class to make writing discord commands a bit more easy
 */
public class CommandContext implements ICommandContext {
	private final GuildMessageReceivedEvent event;
	private final List<String> args;

	public CommandContext(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final List<String> args) {
		this.event = event;
		this.args = args;
	}

	public Guild getGuild() {
		return this.getEvent().getGuild();
	}

	public GuildMessageReceivedEvent getEvent() {
		return this.event;
	}

	public List<String> getArgs() {
		return args;
	}

	@Override
	public MessageChannel getChannel() {
		return event.getChannel();
	}

	public Member getMember() {
		return event.getMember();
	}

	public Member getSelfMember() {
		return event.getGuild().getMember(event.getJDA().getSelfUser());
	}

	public User getAuthor() {
		return event.getAuthor();
	}

	public ShardManager getShardManager() {
		return event.getJDA().getShardManager();
	}

	public JDA getJDA() {
		return event.getJDA();
	}

	public SelfUser getSelfUser() {
		return event.getJDA().getSelfUser();
	}

	public TextChannel getGuildChannel() {
		return event.getChannel();
	}

	public Message getMessage() {
		return event.getMessage();
	}

	@Override
	public long getMaxFileSize() {
		return getGuild().getMaxFileSize();
	}
}
