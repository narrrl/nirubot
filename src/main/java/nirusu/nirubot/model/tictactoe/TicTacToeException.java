package nirusu.nirubot.model.tictactoe;

import java.io.Serial;

public class TicTacToeException extends RuntimeException {
    /**
     * Generated UID
     */
    @Serial
    private static final long serialVersionUID = -254326956542368994L;

    public TicTacToeException(String cause) {
        super(cause);
    }

}
