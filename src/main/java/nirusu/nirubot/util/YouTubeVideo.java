package nirusu.nirubot.util;

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
            public Default default_;
            public class Default {
                private String url;
            }
        }
    }


    public String getThumbnailUrl() {
        return this.snippet.thumbnails.default_.url;
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
