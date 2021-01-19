package nirusu.nirubot.util.youtubedl;

import javax.annotation.Nonnull;
import java.io.Serial;

public class InvalidYoutubeDlException extends Exception {

    @Serial
    private static final long serialVersionUID = 7492321069342154767L;

    public InvalidYoutubeDlException() {
        super();
    }

    public InvalidYoutubeDlException(@Nonnull String str) {
        super(str);
    }

}
