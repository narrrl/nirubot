package nirusu.nirubot.command.fun.moasic;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.command.IPrivateCommand;
import nirusu.nirubot.command.PrivateCommandContext;

import java.util.Collections;
import java.util.List;

public class Mosaic implements IPrivateCommand {

    @Override
    public void execute(PrivateCommandContext ctx) {
        ctx.reply("Softwaretechnik ist die Lehre von der Softwarekonstruktion\\: der systematischen Entwicklung und Pflege von Softwaresystemen");
    }

    @Override
    public MessageEmbed helpMessage() {
        return ICommand.createHelp("this command creates a mosaic from the given input image", "", this);
    }

    @Override
    public List<String> alias() {
        return Collections.singletonList("tichy");
    }

    @Override
    public void execute(CommandContext ctx) {

    }
}
