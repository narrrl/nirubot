package nirusu.nirubot.command;

import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Permission;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.help.CommandMeta;
import nirusu.nirubot.core.help.HelpCreator;
import nirusu.nirubot.util.DiscordUtil;
import nirusu.nirubot.util.RandomHttpClient;
import nirusu.nirubot.util.arknight.RecruitmentCalculator;
import nirusu.nirubot.util.arknight.TagCombination;
import nirusu.nirubot.util.gelbooru.Gelbooru;
import nirusu.nirubot.util.gelbooru.Image;
import nirusu.nirubot.util.gelbooru.Option;
import nirusu.nirubot.util.gelbooru.PostTag;
import nirusu.nirubot.util.nekolove.NekoLove;
import nirusu.nirubot.util.nekolove.NekoLove.NekoLoveImage;
import nirusu.nirubot.util.tictactoe.TicTacToeHandler.TicTacToeCommand;
import nirusu.nirubot.util.youtubedl.YoutubeDLHandler;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class FunModule extends BaseModule {
    private static final String JAVA_VERSION = System.getProperty("java.version");
    private static final String OS = String.format("%s - %s", System.getProperty("os.name"),
            System.getProperty("os.version"));
    private static final String ARCH = System.getProperty("os.arch");
    private static final int MAX_PROCS = Runtime.getRuntime().availableProcessors();

    @Command(key = "mock", description = "Random upper and lower cases")
    public void mock() {
        String message = String.join(" ", ctx.getArgs().orElse(Collections.emptyList()));

        if (message.equals("")) {
            return;
        }
        List<Byte> nums;
        try {
            nums = RandomHttpClient.getRandomBit(message.length());
        } catch (IOException e) {
            return;
        }
        Iterator<Byte> it = nums.iterator();
        StringBuilder builder = new StringBuilder();
        char[] ch = message.toCharArray();
        for (int i = 0; i < message.length() && it.hasNext(); i++) {
            byte num = it.next();
            char c = num == 0 ? Character.toUpperCase(ch[i]) : Character.toLowerCase(ch[i]);
            builder.append(c);
        }
        ctx.reply(builder.toString());
        ctx.getSelfMember().flatMap(u -> u.getBasePermissions().blockOptional()).ifPresent(perms -> {
            if (perms.contains(Permission.ADMINISTRATOR) || perms.contains(Permission.MANAGE_MESSAGES)) {
                ctx.getEvent().getMessage().delete().block();
            }
        });
    }

    /**
     * ttt start [player]: Start a New Game<br>
     * ttt put [index]: put your piece to a given index<br>
     * ttt stop: reset the game<br>
     */
    @Command(key = { "tictactoe", "ttt" }, description = "Play TicTacToe", context = { Channel.Type.GUILD_CATEGORY,
            Channel.Type.GUILD_NEWS, Channel.Type.GUILD_TEXT })
    public void tictactoe() {
        ctx.getArgs().ifPresent(args -> {

            if (args.isEmpty()) {
                return;
            }

            String key = args.get(0);

            TicTacToeCommand.get(key).exec(ctx);
        });
    }

    @Command(key = { "ytd", "youtubedl", "youtubedownload",
            "ytdownload" }, description = "Downloads youtube videos with youtubedl")
    public void youtubedl() {
        ctx.getArgs().ifPresent(args -> ctx.getAuthor().ifPresent(author -> {
            if (!YoutubeDLHandler.getInstance().startDownload(ctx, args, author)) {
                ctx.reply("You can only download one video at a time");
                return;
            }
            ctx.reply("Started downloading and converting! This might take some time");
        }));
    }

    @Command(key = { "ark", "arknights",
            "arkcalc" }, description = "Calculates the best possible tag combinations for given input")
    public void arknights() {
        ctx.getArgs().ifPresent(args -> {
            if (args.size() > 15 || args.size() < 2) {
                return;
            }
            List<TagCombination> all = RecruitmentCalculator.getRecruitment().calculate(args, ctx.getUserInput());

            Collections.reverse(all);
            ctx.getChannel().ifPresent(ch -> {
                boolean first = true;
                for (String str : RecruitmentCalculator.formatForDiscord(all)) {
                    if (first) {
                        ch.createEmbed(emb -> emb.setDescription(str).setColor(Nirubot.getColor())
                                .setTitle("All Combinations:")).block();
                        first = false;
                    } else {
                        ch.createEmbed(emb -> emb.setDescription(str).setColor(Nirubot.getColor())).block();

                    }
                }
            });
        });
    }

    @Command(key = { "neko", "nya", }, description = "Nyaaa~~")
    public void neko() {
        ctx.getArgs().ifPresent(args -> {
            if (args.isEmpty()) {
                NekoLoveImage image;
                try {
                    image = NekoLove.getNekoLoveImage("neko");
                } catch (IllegalArgumentException e) {
                    ctx.reply(e.getMessage());
                    return;
                }
                ctx.getChannel().ifPresent(ch -> ch.createEmbed(emb -> emb.setImage(image.url())
                        .setColor(Nirubot.getColor()).setTitle(String.format("Here is your %s", "Neko"))).block());
            }
        });
    }

    @Command(key = "hug", description = "Hug another person!")
    public void hug() {
        ctx.getArgs().ifPresent(args -> ctx.getAuthor().ifPresent(author -> {
            if (args.size() == 1) {
                NekoLoveImage image;
                try {
                    image = NekoLove.getNekoLoveImage("hug");
                } catch (IllegalArgumentException e) {
                    ctx.reply(e.getMessage());
                    return;
                }

                User user = ctx.getEvent().getMessage().getUserMentions().collectList().blockOptional()
                        .map(list -> list.stream().findFirst().orElse(author)).orElse(author);

                ctx.getChannel().ifPresent(ch -> ch
                        .createEmbed(emb -> emb.setImage(image.url()).setColor(Nirubot.getColor())
                                .setDescription(String.format("%s hugs %s", author.getMention(), user.getMention())))
                        .block());
            }
        }));
    }

    @Command(key = "nakiri", description = "Get some cute nakiri in your life")
    public void nakiri() {
        ctx.getArgs().ifPresent(args -> {
            if (!args.isEmpty())
                return;
            Gelbooru.getSafeNakiri()
                    .ifPresent(img -> DiscordUtil.sendEmbed(ctx, spec -> spec.setTitle("Here is your cute Nakiri")
                            .setUrl(img.getSource()).setColor(Nirubot.getColor()).setImage(img.getUrl())));
        });
    }

    @Command(key = { "animepic", "pic", "image" }, description = "Get some anime pics")
    public void animepic() {
        ctx.getArgs().ifPresent(args -> {
            if (args.isEmpty()) {
                return;
            }

            String search = String.join(" ", args);
            List<String> tagList = Gelbooru.searchForTags(List.of(search.split(", "))).stream().map(PostTag::getTagName)
                    .collect(Collectors.toList());
            Gelbooru.getImageFor(new Option.Tag(tagList), Image.Rating.SAFE).ifPresentOrElse(this::sendImageToDiscord,
                    () -> ctx.reply("Nothing found"));
        });
    }

    @Command(key = "hentai", description = "Oh hell no")
    public void hentai() {
        ctx.getArgs().ifPresent(args -> {
            if (args.isEmpty()) {
                return;
            }

            if (ctx.getChannel().map(ch -> ch instanceof TextChannel && !((TextChannel) ch).isNsfw()).orElse(true)) {
                ctx.reply("Not here, pervert <a:Lewd:802272997792546907>");
                return;
            }

            String search = String.join(" ", args);
            List<String> tagList = Gelbooru.searchForTags(List.of(search.split(", "))).stream().map(PostTag::getTagName)
                    .collect(Collectors.toList());
            if (tagList.isEmpty()) {
                ctx.reply("Noting found");
                return;
            }
            Gelbooru.getImageFor(new Option.Tag(tagList), Image.Rating.EXPLICIT)
                    .ifPresentOrElse(this::sendImageToDiscord, () -> ctx.reply("Nothing found"));
        });
    }

    private void sendImageToDiscord(Image img) {
        if (img.hasTag("video")) {
            ctx.reply(img.getUrl());
            return;
        }
        DiscordUtil.sendEmbed(ctx,
                spec -> spec.setTitle("Here is your cute anime girl:").setUrl(img.getPostUrl())
                        .setColor(Nirubot.getColor()).setImage(img.getUrl())
                        .setFooter(String.format("Source: %s", img.getSource()), ""));
    }

    @Command(key = "ping", description = "This command pings the bot")
    public void ping() {
        ctx.getArgs().ifPresent(args -> {
            if (!args.isEmpty())
                return;

            long curr = System.currentTimeMillis();
            ctx.reply("Pong!").ifPresent(mes -> {
                long ping = mes.getTimestamp().toEpochMilli() - curr;
                // not very accurate ¯\_(ツ)_/¯
                mes.edit(edit -> edit.setContent(String.format("Pong: %d ms", ping))).block();
            });
        });
    }

    @Command(key = { "help", "h" }, description = "Help command")
    public void help() {
        ctx.getArgs().ifPresent(args -> {
            HelpCreator h = Nirubot.getNirubot().getHelpCreator();
            String help;
            String title;
            String footNote;
            String prefix = ctx.getGuild().map(guild -> GuildManager.of(guild.getId()).prefix()).orElse("");
            switch (args.size()) {
                case 0 -> {
                    // just all commands
                    help = h.listModules();
                    title = "All Command-Modules";
                    footNote = String.format("List all commands of a module with `%shelp <module>`", prefix);
                }
                case 1 -> {
                    // get all commands of that module
                    help = h.getModuleWithName(args.get(0)).map(h::listCommandsFor)
                            .orElse(String.format("No module %s found", args.get(0)));
                    // set title
                    title = String.format("All Commands of Module %s", args.get(0));
                    // help to get more info about a command in that module
                    footNote = String.format("Get info about a command with `%shelp %s <command>`", prefix,
                            args.get(0));
                }
                case 2 -> {
                    // get meta data for command
                    CommandMeta.Metadata data = h.getModuleWithName(args.get(0))
                            .map(module -> h.metadataForCommand(module, args.get(1)))
                            .orElse(new CommandMeta.Metadata().setName("")
                                    .setDescription(String.format("No module %s found", args.get(0))).setSyntax(""));
                    // set title for embed
                    title = String.format("Help for Command %s", args.get(1));
                    // help is description and a list of aliases for that command
                    help = data.getDescription().equals("Command not found") ? data.getDescription()
                            : data.getDescription().concat("\nAliases: ").concat(data.getAliases());
                    // foot note is the syntax for that command
                    footNote = data.getSyntax().equals("") ? ""
                            : "Syntax: \n`".concat(data.getSyntax().replace("<prefix>", prefix)).concat("`");
                }
                // if args size is not right return and do nothing
                default -> {
                    return;
                }
            }

            // create embed and send it
            ctx.getChannel()
                    .ifPresent(ch -> ch.createEmbed(spec -> spec.setTitle(title)
                            .setDescription(footNote.equals("") ? help : help.concat("\n\n".concat(footNote)))
                            .setColor(Nirubot.getColor())).block());
        });
    }

    @Command(key = "avatar", description = "Gets the avatar of the tagged user")
    public void avatar() {
        ctx.getArgs().ifPresent(args -> {
            if (args.size() != 1) {
                return;
            }

            User u = ctx.getEvent().getMessage().getUserMentions().blockFirst();

            // get avatar url and add the size tag to get a bigger size
            ctx.getChannel().ifPresent(
                    ch -> ch.createEmbed(specs -> specs.setImage(u.getAvatarUrl().concat("?size=2048"))).block());
        });
    }

    @Command(key = "invite", description = "Sends a invite for the bot, to invite it to your server")
    public void invite() {
        if (!ctx.getArgs().map(List::isEmpty).orElse(false)) {
            return;
        }

        DiscordUtil.sendEmbed(ctx, spec -> spec.setTitle("Invite this bot to your server!")
                .setUrl(String.format("https://discord.com/api/oauth2/authorize?client_id=%d&permissions=8&scope=bot",
                        ctx.getSelf().map(self -> self.getId().asLong()).orElse(-1L)))
                .setImage("https://media1.tenor.com/images/b7254b1f7083b0d8088905de997ef5bb/tenor.gif"));

    }

    @Command(key = "info", description = "Get the current application informations")
    public void info() {
        ctx.getArgs().ifPresent(args -> {
            if (!args.isEmpty()) {
                return;
            }

            DiscordUtil.sendEmbed(ctx, spec -> spec.setTitle(ctx.getSelf().map(User::getUsername).orElse(""))
                    .setDescription(getApplicationInfo()).setColor(Nirubot.getColor())
                    .setThumbnail(ctx.getSelf().map(User::getAvatarUrl).orElse("")).setTimestamp(Instant.now()));

        });
    }

    private synchronized String getApplicationInfo() {
        StringBuilder owners = new StringBuilder();
        for (long l : Nirubot.getConfig().getOwners()) {
            owners.append("<@").append(l).append(">").append(", ");
        }
        String ownerString = owners.length() < 2 ? "" : owners.substring(0, owners.length() - 2);
        int load = 0;
        try {
            String[] cmdline = { "sh", "-c", "echo $(vmstat 1 2|tail -1|awk '{print $15}')" };
            Process pr = Runtime.getRuntime().exec(cmdline);
            StringBuilder output = new StringBuilder();
            try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()))) {
                // Read the output from the command
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    output.append(s);
                }
            }
            load = 100 - Integer.parseInt(output.toString());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        String loadString = getUsageBar(load);
        return String.format(
                "**Java Version:** %s%n**OS:** %s%n**Bot-Owner:** %s%n**Used Memory:** %dMb%n**Cores:** %d%n**Architecture:** %s%n**CPU-Usage:** %s",
                JAVA_VERSION, OS, ownerString,
                ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000), MAX_PROCS, ARCH,
                load + "%\n" + loadString);
    }

    private String getUsageBar(int usage) {
        StringBuilder usageBar = new StringBuilder();
        for (int i = 10; i <= 100; i += 10) {
            if (i <= usage) {
                usageBar.append("█");
            } else {
                usageBar.append("░");
            }
        }
        return usageBar.toString();
    }
}
