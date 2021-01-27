package nirusu.nirubot.util.youtubedl;

import com.sapher.youtubedl.YoutubeDLRequest;

enum Option {
    AUDIO("-audio") {
        @Override
        public void exec(YoutubeDLRequest request) {
            request.setOption("extract-audio");
            request.setOption("audio-format", "mp3");
            request.setOption("embed-thumbnail");
        }
    },
    VIDEO("-video") {
        @Override
        public void exec(YoutubeDLRequest request) {
            request.setOption("recode-video", "mp4");
        }
    },
    BEST("-best") {
        @Override
        public void exec(YoutubeDLRequest request) {
            request.setOption("format", "best");
        }
    };

    public abstract void exec(final YoutubeDLRequest request);

    private final String option;

    Option(final String op) {
        this.option = op;
    }

    public static Option getOption(final String option) {
        for (Option o : Option.values()) {
            if (o.option.equals(option)) {
                return o;
            }
        }
        throw new IllegalArgumentException(option + " is not an option");
    }
}
