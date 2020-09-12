package nirusu.nirubot.command.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        if (args.size() > 7 || args.size() < 2) {
            return;
        }
        List<String> tags = new ArrayList<>();

        for (int i = 1; i < args.size(); i++) {
            tags.add(args.get(i));
        }

        List<TagCombination> all = nirusu.nirubot.util.arknight.Recruitment.getRecruitment().calculate(tags);

        Collections.reverse(all);

        StringBuilder builder = new StringBuilder();
        all.forEach(str -> builder.append(str).append("\n"));
        EmbedBuilder emb = new EmbedBuilder();
        if (builder.length() == 0) {
            builder.append("No combinations found! Check if you spelled the tags right ");
        }
        emb.setColor(Nirubot.getColor()).setTitle("All combinations:").setDescription(builder.substring(0,builder.length() - 1));
        ctx.reply(emb.build());
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
