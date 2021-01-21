package nirusu.nirubot.util.gelbooru;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;

import nirusu.nirubot.Nirubot;
import nirusu.nirubot.util.gelbooru.Image.Rating;
import nirusu.nirubot.util.gelbooru.Option.Tag;
import nirusu.nirubot.util.gelbooru.Option.Count;

public class Gelbooru {
    private static final String API_URL = "https://gelbooru.com/index.php?page=dapi&s=%s&q=index&json=1&tags=";
    private static final String SORT_TAG = "sort:";
    private static final String TAG_SCOPE = "tag";
    private static final String POST_SCOPE = "post";
    private static final String SORT_BY_COUNT = "&orderby=count";
    private static final String TAG_NAME_QUERRY = "&name_pattern=";


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

    public static Optional<List<PostTag>> searchForTag(String searchTerm) {
        String normalOptions = "&name=" + searchTerm.replace(" ", "_");
        String optionsString = TAG_NAME_QUERRY + "%" + String.join("%", searchTerm.split(" ")) + "%";
        String optionString2 = TAG_NAME_QUERRY + "%" +
                    List.of(searchTerm.split(" ")).stream()
                            .sorted(Comparator.reverseOrder())
                            .collect(Collectors.joining("%")) + "%";
        return getJson(normalOptions + SORT_BY_COUNT, TAG_SCOPE).map(Gelbooru::convertToList)
                .or(() -> getJson(optionsString + SORT_BY_COUNT, TAG_SCOPE).map(Gelbooru::convertToList)
                        .or(() -> getJson(optionString2 + SORT_BY_COUNT, TAG_SCOPE).map(Gelbooru::convertToList)));
    }

    public static Set<PostTag> searchForSimilarTags(String searchTerm) {
        Set<PostTag> tagList = new HashSet<>();
        String normalOptions = "&name=" + searchTerm.replace(" ", "_");
        String optionsString = TAG_NAME_QUERRY + "%" + String.join("%", searchTerm.split(" ")) + "%";
        String optionString2 = TAG_NAME_QUERRY + "%" +
                List.of(searchTerm.split(" ")).stream()
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.joining("%")) + "%";
        getJson(normalOptions + SORT_BY_COUNT, TAG_SCOPE).map(Gelbooru::convertToList).ifPresent(tagList::addAll);
        getJson(optionsString + SORT_BY_COUNT, TAG_SCOPE).map(Gelbooru::convertToList).ifPresent(tagList::addAll);
        getJson(optionString2 + SORT_BY_COUNT, TAG_SCOPE).map(Gelbooru::convertToList).ifPresent(tagList::addAll);
        return tagList;
    }

    private static List<PostTag> convertToList(String json) {
        return Nirubot.getGson().fromJson(json, new TypeToken<List<PostTag>>() {
        }.getType());
    }

    public static List<PostTag> searchForTags(List<String> searchTerms) {
        List<PostTag> tags = new ArrayList<>();
        for (String s : searchTerms) {
            searchForTag(s).ifPresent(list -> {
                if (list.isEmpty()) {
                    return;
                }
                tags.add(list.get(0));
            });
        }
        return tags;
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
            con.disconnect();

        } catch (IOException e) {
            if (con != null)
                con.disconnect();
            return Optional.empty();
        }
        return json.toString().equals("[]") ? Optional.empty() : Optional.of(json.toString());
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
