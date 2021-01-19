package nirusu.nirubot.util.gelbooru;

import java.util.Collections;
import java.util.List;

public interface Option {
    String getTagFormatted();

    public class Tag implements Option {
        private final List<String> tags;

        public Tag(List<String> tags) {
            this.tags = Collections.unmodifiableList(tags);
        }

        @Override
        public String getTagFormatted() {
            return "tags=".concat(String.join("+", tags));
        }
    }

    public class Count implements Option {
        private final int amount;

        public Count(int amount) {
            this.amount = amount;
        }

        @Override
        public String getTagFormatted() {
            return "limit=" + amount;
        }
    }

    public class Page implements Option {

        private final int pageNumber;

        public Page(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        @Override
        public String getTagFormatted() {
            return "pid=" + pageNumber;
        }
    }
}
