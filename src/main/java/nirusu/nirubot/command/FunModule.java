package nirusu.nirubot.command;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import nirusu.nirubot.util.RandomHttpClient;
import nirusu.nirubot.util.youtubedl.InvalidYoutubeDlException;
import nirusu.nirubot.util.youtubedl.YoutubeDl;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

public class FunModule extends BaseModule {

    @Command(key = "mock", description = "Random upper and lower cases")
    public void mock() {
        String message = ctx.getArgs().orElse(Collections.emptyList()).stream().collect(Collectors.joining(" "));

        if (message == null || message.equals("")) {
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

    @Command(key = { "ytd", "youtubedl" }, description = "Downloads youtube videos with youtubedl")
    public void youtubedl() {
        try {
            new YoutubeDl().start(ctx);
        } catch (InvalidYoutubeDlException e) {
            ctx.reply("Woops something went wrong :(");
        }
    }
    
}
