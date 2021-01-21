package nirusu.nirubot.command;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Image.Format;
import nirusu.nirubot.Nirubot;
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
import nirusu.nirubot.util.youtubedl.InvalidYoutubeDlException;
import nirusu.nirubot.util.youtubedl.YoutubeDl;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.CommandContext;
import nirusu.nirucmd.annotation.Command;

public class FunModule extends BaseModule {

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
    }

    @Command(key = { "ytd", "youtubedl", "youtubedownload",
            "ytdownload" }, description = "Downloads youtube videos with youtubedl")
    public void youtubedl() {
        ctx.getArgs().ifPresent(args -> ctx.getAuthor().ifPresent(author -> {
            ctx.reply("Started downloading and converting! This might take some time");
            new Thread(() -> {
                File out;
                try {
                    out = new YoutubeDl(args).start();
                } catch (InvalidYoutubeDlException e) {
                    if (e.getMessage() != null) {
                        ctx.reply(String.format("Error: %s", e.getMessage()));
                    } else {
                        e.printStackTrace();
                    }
                    return;
                }
                if (out.length() > CommandContext.getMaxFileSize() || out.isDirectory()) {
                    ctx.reply(String.format("You can download %s here: %s%s %s", out.getName(),
                            Nirubot.getHost() + Nirubot.getTmpDirPath(), out.getName(), author.getMention()));
                    return;
                }
                ctx.sendFile(out);
            }).start(); // there he goes
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
            Gelbooru.getImageFor(new Option.Tag(tagList), Image.Rating.SAFE)
                    .ifPresentOrElse(img -> DiscordUtil.sendEmbed(ctx,
                            spec -> spec.setTitle("Here is your cute anime girl:").setUrl(img.getSource())
                                    .setColor(Nirubot.getColor()).setImage(img.getUrl())
                                    .setFooter(String.join(", ", img.getTags()), ctx.getGuild()
                                            .map(g -> g.getIconUrl(Format.PNG).orElse(""))
                                            .orElse(""))),
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
                ctx.reply("Not here, pervert <a:Lewd:393566157287981058>");
                return;
            }

            String search = String.join(" ", args);
            List<String> tagList = Gelbooru.searchForTags(List.of(search.split(", "))).stream().map(PostTag::getTagName)
                    .collect(Collectors.toList());
            Gelbooru.getImageFor(new Option.Tag(tagList), Image.Rating.EXPLICIT)
                    .ifPresentOrElse(
                            img -> DiscordUtil.sendEmbed(ctx,
                                    spec -> spec.setTitle("Here is your cute anime girl:").setUrl(img.getSource())
                                            .setColor(Nirubot.getColor()).setImage(img.getUrl())
                                            .setFooter(String.join(", ", img.getTags()), ctx.getGuild()
                                                    .map(g -> g.getIconUrl(Format.PNG).orElse("")).orElse(""))),
                            () -> ctx.reply("Nothing found"));
        });
    }

    @Command(key = { "tags", "tagsearch" }, description = "Gives you information about a searched tag")
    public void tagsearch() {
        ctx.getArgs().ifPresent(args -> {
            if (args.isEmpty()) {
                return;
            }
            String search = String.join(" ", args);
            Set<PostTag> list = Gelbooru.searchForSimilarTags(search);
            if (list.isEmpty()) {
                ctx.reply("Noting found");
                return;
            }
            String desc = list.stream().filter(i -> i.getCount() > 20)
                    .sorted(Comparator.comparingInt(PostTag::getCount)).map(PostTag::toString)
                    .collect(Collectors.joining("\n"));
            String finalList = desc.length() < 2048 ? desc.replace("_", "\\_")
                    : desc.substring(0, 2048).replace("_", "\\_");
            DiscordUtil.sendEmbed(ctx, spec -> spec.setDescription(finalList).setColor(Nirubot.getColor()));
        });
    }

}
