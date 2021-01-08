package nirusu.nirubot.command;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.CommandDispatcher;
import nirusu.nirucmd.annotation.Command;

import java.lang.reflect.Method;

public class HelpModule extends BaseModule {

    @Command( 
        key = "ping", 
        description = "This command pings the bot")
    public void ping() {
        ctx.getArgs().ifPresent(args -> {
            if (!args.isEmpty()) return;

            long curr = System.currentTimeMillis();
            ctx.reply("Pong!").ifPresent(mes -> {
                long ping = mes.getTimestamp().toEpochMilli() - curr;
                // not very accurate ¯\_(ツ)_/¯
                mes.edit(edit -> edit.setContent(String.format("Pong: %d ms", ping))).block();
            });
        });
    }

    @Command( key = {"help", "h"}, description = "Help command")
    public void help() {
        ctx.getArgs().ifPresent(args -> {
            CommandDispatcher dispatcher = Nirubot.getNirubot().getDispatcher();
            Set<Class<? extends BaseModule>> modules = dispatcher.getModules();
            final String desc;
            final String title;
            final String prefix = ctx.getGuild().map(g -> GuildManager.getManager(g.getId().asLong()).prefix()).orElse("");
            StringBuilder str = new StringBuilder();
            if (args.isEmpty()) {
                title = "Nirubot Modules:";
                for (Class<? extends BaseModule> clazz : modules) {
                    str.append(clazz.getSimpleName().toLowerCase().replace("module", "")).append(", ");
                }
                desc = String.format("%s %n%n List all commands of a module with `%shelp <module>`", 
                    str.substring(0, str.length() - 2) , prefix);
            } else if (args.size() == 1) {
                String module = args.get(0);
                List<Method> commands = null;
                for (Class<? extends BaseModule> clazz :modules) {
                    if (clazz.getSimpleName().toLowerCase().replace("module", "").equals(module)) {
                        commands = 
                            Stream.of(clazz.getMethods()).filter(m -> m.isAnnotationPresent(Command.class))
                            .collect(Collectors.toList());
                    }
                }

                if (commands == null) {
                    ctx.reply(String.format("Couldn't find module: %s", module));
                    return;
                }

                title = String.format("Commands for %s:", module);
                for (Method m : commands) {
                    str.append(m.getName()).append(", ");
                }
                desc = str.substring(0, str.length() - 2);
            } else if (args.size() == 2) {
                String module = args.get(0);
                String command = args.get(1);
                List<Method> commands = null;
                for (Class<? extends BaseModule> clazz :modules) {
                    if (clazz.getSimpleName().toLowerCase().replace("module", "").equals(module)) {
                        commands = 
                            Stream.of(clazz.getMethods()).filter(m -> m.isAnnotationPresent(Command.class))
                            .collect(Collectors.toList());
                    }
                }

                if (commands == null) {
                    ctx.reply(String.format("Couldn't find module: %s", module));
                    return;
                }

                title = String.format("Help for command %s:", command);
                Method actualCommand = null;
                for (Method m : commands) {
                    if (m.getName().equals(command)) {
                        actualCommand = m;
                    }
                }
                if (actualCommand == null) {
                    ctx.reply(String.format("No command %s in %s found!", command, module));
                    return;
                }

                desc = String.format("Aliases: %s%n%nDescription: %s", 
                    Stream.of(actualCommand.getAnnotation(Command.class).key()).collect(Collectors.joining(", ")), actualCommand.getAnnotation(Command.class).description());
            } else {
                return;
            }

            ctx.getChannel().ifPresent(ch -> 
                ch.createEmbed(emb -> emb.setTitle(title).setColor(Nirubot.getColor()).setDescription(desc)).block());
        });

    }
}
