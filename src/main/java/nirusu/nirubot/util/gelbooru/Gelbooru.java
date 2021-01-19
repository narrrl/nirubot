package nirusu.nirubot.util.gelbooru;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import com.google.gson.reflect.TypeToken;

import nirusu.nirubot.Nirubot;
import nirusu.nirubot.util.gelbooru.Image.Rating;
import nirusu.nirubot.util.gelbooru.Option.Tag;
import nirusu.nirubot.util.gelbooru.Option.Count;

public class Gelbooru {
    private static final String API_URL = "https://gelbooru.com/index.php?page=dapi&s=%s&q=index&json=1&tags=";
    private static final String RATING_TAG = "rating:";
    private static final String SORT_TAG = "sort:";
    private static final String TAG_SCOPE = "tag";
    private static final String POST_SCOPE = "post";

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
        return getJson(optionsString.toString(), POST_SCOPE)
                .map(js -> Nirubot.getGson().fromJson(js, new TypeToken<List<Image>>() {
                }.getType()));
    }

    public static Optional<Image> getImageFor(Tag tag, Rating r) {
        return getImagesFor(tag, 1, r, true).map(list -> {
            if (list.isEmpty()) {
                return null;
            }
            return list.get(0);
        });
    }

    public static Optional<PostTag> searchForTag(String searchTerm) {
        String optionsString = "&name_pattern=%" + String.join("%", searchTerm.split(" ")) + "%";
        return getJson(optionsString, TAG_SCOPE).map(js -> {
            List<PostTag> tags = Nirubot.getGson().fromJson(js, new TypeToken<List<PostTag>>() {
            }.getType());
            if (tags.isEmpty()) {
                return null;
            }
            return tags.get(0);
        });
    }

    private static Optional<String> getJson(String optionsString, String scope) {
        StringBuilder json = new StringBuilder();
        HttpURLConnection con = null;
        try {
            URL url = new URL(String.format(API_URL, scope).concat(optionsString));
            con = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                json.append(line);
            }
            in.close();

        } catch (IOException e) {
            if (con != null)
                con.disconnect();
            return Optional.empty();
        }
        return Optional.of(json.toString());
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
}
