package nirusu.nirubot.util.youtubedl;

enum Option {
        AUDIO("-audio") {
            @Override
            public void exec(YoutubeDl cmd) {
                if (cmd.formatIsSet)
                    throw new IllegalArgumentException("You can't use -audio and -video in one request");
                cmd.req.setOption("extract-audio");
                cmd.req.setOption("audio-format", "mp3");
                cmd.req.setOption("embed-thumbnail");
                cmd.formatIsSet = true;
            }
        },
        VIDEO("-video") {
            @Override
            public void exec(YoutubeDl cmd) {
                if (cmd.formatIsSet)
                    throw new IllegalArgumentException("You can't use -audio and -video in one request");
                cmd.req.setOption("recode-video", "mp4");
                cmd.formatIsSet = true;
            }
        },
        BEST("-best") {
            @Override
            public void exec(YoutubeDl cmd) {
                cmd.req.setOption("format", "best");
            }
        };

        public abstract void exec(final YoutubeDl cmd);

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
