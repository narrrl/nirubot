package nirusu.nirubot.command.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;

public class SetAvatar implements ICommand {

    @Override
    public void execute(CommandContext ctx) {
        if (Nirubot.isOwner(ctx.getAuthor().getIdLong())) {
            List<Message.Attachment> attachment = ctx.getMessage().getAttachments();
            if (attachment.size() != 1) {
                ctx.reply("You must attach an image");
                return;
            }
            try {
                InputStream s = new URL(attachment.get(0).getUrl()).openStream();
                ctx.getJDA().getSelfUser().getManager().setAvatar(Icon.from(s)).queue();
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            ctx.reply("Updated avatar!");
        } else {
            ctx.reply("Not enough permissions");
        }
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Sets the avatar for the bot. You have to attach a picture", gm.prefix(), this);
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("setav");
    }
    
}
