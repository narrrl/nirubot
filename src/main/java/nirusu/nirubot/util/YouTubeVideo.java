package nirusu.nirubot.util;

import com.google.gson.annotations.SerializedName;

/**
 * This class is the skeleton for a youtube video json that get requested in
 * {@link nirusu.nirubot.command.fun.music.YTSearch#execute(nirusu.nirubot.command.CommandContext)}
 * 
 */
public class YouTubeVideo {
    private Id id;
    private Snippet snippet;

    public class Id {
        private String videoId;
        private String kind;
    }

    public class Snippet {
        private String title;
        private Thumbnails thumbnails;

        public class Thumbnails {
            @SerializedName("default")
            public Default defaultThumbnail;

            public class Default {
                private String url;
            }
        }
    }

    public String getThumbnailUrl() {
        return this.snippet.thumbnails.defaultThumbnail.url;
    }

    public String getVideoId() {
        return this.id.videoId;
    }

    public String getTitle() {
        return this.snippet.title;
    }

    public String getKind() {
        return this.id.kind;
    }
}
