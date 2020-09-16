package nirusu.nirubot.command.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.util.arknight.Operator;
import nirusu.nirubot.util.arknight.TagCombination;

public class Recruitment implements ICommand {

    @Override
    public void execute(CommandContext ctx) {
        List<String> args = ctx.getArgs();

        if (args.size() >  15 || args.size() < 2) {
            return;
        }

        List<String> tags = new ArrayList<>();
        StringBuilder b = new StringBuilder();

        for (int i = 1; i < args.size(); i++) {
            tags.add(args.get(i));
            b.append(args.get(i));
            if (i + 1  < args.size()) {
                b.append(" ");
            }
        }

        List<TagCombination> all = nirusu.nirubot.util.arknight.Recruitment
        .getRecruitment().calculate(tags, b.toString());

        Collections.reverse(all);

        EmbedBuilder emb = new EmbedBuilder();
        emb.setColor(Nirubot.getColor()).setTitle("All combinations:");
        StringBuilder builder = new StringBuilder();

        for (TagCombination cb : all) {

            // if the tag combination string is bigger than discord limit
            if (cb.toString().length() > 2000) {

                // checks if the stringbuilder is not empty and sends the message to clear it
                if (builder.length() != 0) {
                    emb.setDescription(builder.toString());
                    ctx.reply(emb.build());
                    builder = new StringBuilder();
                    emb = new EmbedBuilder();
                    emb.setColor(Nirubot.getColor());
                }

                // creates a new stringbuilder tmp to get one big string
                Iterator<String> i = cb.toStringAsList().iterator();

                // append string from the list
                while (i.hasNext()) {
                    String n = i.next();

                    // when the string is longer than the discord limit
                    // send it and continue building the string
                    if (builder.length() + n.length() > 2000) {
                        emb.setDescription(builder.substring(0, builder.length() - 1));
                        ctx.reply(emb.build());
                        builder = new StringBuilder();
                        emb = new EmbedBuilder();
                        emb.setColor(Nirubot.getColor());
                    }
                    builder.append(n).append(" ");
                }

                // send rest of the string
                if (builder.length() > 0) {
                    emb.setDescription(builder.substring(0, builder.length() - 1));
                    ctx.reply(emb.build());
                    builder = new StringBuilder();
                    emb = new EmbedBuilder();
                    emb.setColor(Nirubot.getColor());

                }

            } else {

                // string would be longer than discord limit
                // send it and continue
                if (builder.length() + cb.toString().length() > 1800) {
                    emb.setDescription(builder.toString());
                    ctx.reply(emb.build());
                    builder = new StringBuilder();
                    emb = new EmbedBuilder();
                    emb.setColor(Nirubot.getColor());
                }
                builder.append(cb).append("\n\n");
            }
        }

        // send rest of the string
        if (builder.length() > 0) {
            emb.setDescription(builder.toString());
            ctx.reply(emb.build());
        }
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("rec");
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Gets the best possible combinations for up to 6 tags. Tags are:\n" + Operator.getAllTagsAsString(), gm.prefix(), this);
    }

}
