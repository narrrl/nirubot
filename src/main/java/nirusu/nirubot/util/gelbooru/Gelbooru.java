package nirusu.nirubot.util.gelbooru;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;

import nirusu.nirubot.Nirubot;
import nirusu.nirubot.util.gelbooru.Image.Rating;
import nirusu.nirubot.util.gelbooru.Option.Tag;
import nirusu.nirubot.util.gelbooru.Option.Count;
import org.checkerframework.checker.nullness.Opt;

public class Gelbooru {
    private static final String API_URL = "https://gelbooru.com/index.php?page=dapi&s=post&q=index&json=1&tags=";
    private static final String RATING_TAG = "rating:";
    private static final String SORT_TAG = "sort:";

    private Gelbooru() {
        throw new IllegalAccessError();
    }

    public static Optional<List<Image>> getImagesFor(Tag tag, int amount, Rating r, boolean random) {
        StringBuilder optionsString = new StringBuilder();
        optionsString.append("&").append(tag.getTagFormatted()).append("+").append(r.toString());
        if (random) {
            optionsString.append("+").append(SORT_TAG).append("random");
        }
        Count c = new Count(amount);

        optionsString.append("&").append(c.getTagFormatted());
        StringBuilder json = new StringBuilder();
        HttpURLConnection con = null;
        try {
            URL url = new URL(API_URL.concat(optionsString.toString()));
            con = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                json.append(line);
            }
            in.close();

        } catch (IOException e) {
            if (con != null) con.disconnect();
            return Optional.empty();
        }
        List<Image> images = Nirubot.getGson().fromJson(json.toString(), new TypeToken<List<Image>>() {
        }.getType());
        return Optional.ofNullable(images);

    }

    public Optional<PostTag> searchForTag(String searchTerm) {
        return Optional.empty();
    }

    public static Optional<Image> getSafeNakiri() {
        Optional<List<Image>> images = getImagesFor(new Tag(List.of("nakiri_ayame")), 1, Rating.SAFE, true);

        return images.map(list -> {
            if (!list.isEmpty()) {
                return list.get(0);
            }
            return null;
        });
    }


    public static void main(String[] args) {
        getSafeNakiri().ifPresent(nakiri -> System.out.println(nakiri.getUrl()));
    }

}
