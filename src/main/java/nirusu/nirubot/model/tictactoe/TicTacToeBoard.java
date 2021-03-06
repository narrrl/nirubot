package nirusu.nirubot.model.tictactoe;

/**
 * This class represents a basic tic tac toe board. All fields are filled with
 * {@link nirusu.nirubot.model.tictactoe.Player#empty()}
 */
public class TicTacToeBoard {
    private final Player[][] board;

    public TicTacToeBoard(int boardSize) {
        if (boardSize < 1) {
            throw new TicTacToeException("A board should be equal or bigger than 1x1");
        }
        board = new Player[boardSize][boardSize];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = Player.empty();
            }
        }
    }

    /**
     * puts a given @param player at the given coordinates @param x and @param y.
     * 
     * @return true if the player has been placed at the given coordinates or else
     *         if the coordinates where our of bound or if a player was already
     *         there.
     */
    public boolean put(final int x, final int y, Player player) {
        if (x > board.length - 1 || y > board.length - 1) {
            return false;
        }
        if (x < 0 || y < 0) {
            return false;
        }

        if (board[x][y].equals(Player.empty())) {
            board[x][y] = player;
            return true;
        }

        return false;
    }

    public Player get(final int x, final int y) {
        return board[x][y];
    }

    public int size() {
        return board.length;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            for (Player[] players : board) {
                out.append(players[i]);
            }
            out.append("\n");
        }
        return out.toString().trim();
    }
}
