package nirusu.nirucmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import nirusu.nirucmd.annotation.Command;
import nirusu.nirucmd.exception.InvalidContextException;

/**
 * This class represents the current command context (Context (Guild, Private,
 * ...), Event, Message etc.) and contains useful methods to interact with the
 * bot.
 */
public class CommandContext {
    private static final int FILE_SIZE_MAX = 8388119;
    private List<String> args;
    private final Command.Context context;
    private final MessageCreateEvent event;

    /**
     * Creates a new {@link CommandContext}.
     *
     * @param event represents the current event
     */
    public CommandContext(@Nonnull MessageCreateEvent event) {
        this.event = event;
        Channel.Type type = event.getMessage().getChannel().block().getType();
        if (type.equals(Channel.Type.DM)) {
            context = Command.Context.PRIVATE;
        } else if (type.equals(Channel.Type.GUILD_TEXT)) {
            context = Command.Context.GUILD;
        } else {
            throw new InvalidContextException("Invalid Context");
        }
    }

    public void setArgs(@Nonnull List<String> args) {
        this.args = Collections.unmodifiableList(args);
    }

    /**
     * Shortcut for a reply with a given message @param message
     */
    public Message reply(@Nonnull String message) {
        MessageChannel m = event.getMessage().getChannel().block();
        return m.createMessage(message).block();
    }

    /**
     * Checks if the current context is equal to @param context
     *
     * @return true if {@link #context} equals @param context
     */
    public boolean isContext(Command.Context context) {
        return this.context.equals(context);
    }

    public Optional<User> getAuthor() {
        return event.getMessage().getAuthor();
    }

    /**
     * !!! ARGS IS NOT MODIFIABLE !!! {@link Collections#unmodifiableList}
     *
     * @return list of all arguments
     */
    public Optional<List<String>> getArgs() {
        return Optional.ofNullable(args);
    }

    public String getUserInput() {
        if (args == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String str : args) {
            builder.append(str).append(" ");
        }
        if (builder.length() == 0) {
            return "";
        }
        return builder.substring(0, builder.length() - 1);
    }

    public boolean isPrivate() {
        return isContext(Command.Context.PRIVATE);
    }

    public boolean isGuild() {
        return isContext(Command.Context.GUILD);
    }

    public MessageCreateEvent getEvent() {
        return this.event;
    }

    public Optional<Guild> getGuild() {
        return event.getGuild().blockOptional();
    }

    /**
     * Checks if the author has the permission @param p
     * 
     * @return if contex is not guild return false or user doesnt have the
     *         permission
     */
    public boolean hasGuildPermission(Permission p) {
        Optional<PermissionSet> perms = getMember().flatMap(member
            -> member.getBasePermissions().blockOptional());
        if (perms.isPresent()) {
            return perms.get().contains(p);
        }
        return false;
    }

    public Optional<Member> getMember() {
        return getGuild().flatMap( guild 
            -> getAuthor().flatMap(user 
            -> guild.getMemberById(user.getId()).blockOptional())
        );
    }

    public Optional<Message> sendFile(File f) {
        return getChannel().flatMap(ch -> ch.createMessage(mes -> 
            {
            try {
                mes.addFile(f.getName(), new FileInputStream(f));
            } catch (FileNotFoundException e) {
                CommandDispatcher.getLogger().error(e.getMessage(), e);
            }
        }
        ).blockOptional());
	}

	public static long getMaxFileSize() {
        return FILE_SIZE_MAX;
    }
    
    public Optional<MessageChannel> getChannel() {
        return event.getMessage().getChannel().blockOptional();
    }


    public Optional<User> getSelf() {
        return event.getClient().getSelf().blockOptional();
    }

    public Optional<VoiceState> getSelfVoiceState() {
        return getSelfMember().flatMap(self 
            -> self.getVoiceState().blockOptional());
    }

    public Optional<Member> getSelfMember() {
        return getSelf().flatMap(self 
            -> getGuild().flatMap(guild 
            -> guild.getMemberById(self.getId()).blockOptional())
        );
    }

    public Optional<VoiceState> getAuthorVoiceState() {
        return getMember().flatMap(member 
            -> member.getVoiceState().blockOptional());
    }
}
