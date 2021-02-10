package nirusu.nirubot.util.tictactoe;

import java.util.Arrays;

public class TicTacToe {
    private static final String WRONG_PLAYER = "This is not your Turn!";
    private static final String CANNOT_PLACE = "You can't place here";
    private static final String PLAYER_WON = "Player {} won the game!";
    private static final String DRAW = "Draw!";
    private final Player one;
    private final Player two;
    private final TicTacToeBoard board;
    private Player current;
    private boolean isRunning;

    public TicTacToe(Player one, Player two, int boardSize) {
        this.one = one;
        this.two = two;

        //player one always starts
        this.current = one;

        this.board = new TicTacToeBoard(boardSize);
        isRunning = true;
    }

    public synchronized String makeTurn(int x, int y, Player p) {
        if (!p.equals(current)) {
            return WRONG_PLAYER;
        }

        if (!board.put(x, y, p)) {
            return CANNOT_PLACE;
        }

        for (Player pl : Arrays.asList(one, two)) {
            if (hasWone(pl)) {
                isRunning = false;
                return PLAYER_WON.replace("{}", p.toString());
            } else if (isDraw()) {
                isRunning = false;
                return DRAW;
            }
        }

        advanceUser();

        return board.toString();
    }

    //switch the next player to move
    private void advanceUser() {
        if (current.equals(one)) {
            current = two;
            return;
        }
        current = one;
    }


    private boolean hasWone(Player p) {
        int dia = 0;
        int secDia = 0;
        for (int i = 0; i < board.size(); i++) {

            dia += board.get(i, i).numeric();
            secDia += board.get(board.size() - 1 - i, i).numeric();

            if (Math.abs(dia / board.size() - p.numeric()) < 0.01 || Math.abs(secDia / board.size() - p.numeric()) < 0.01) {
                return true;
            }

            int col = 0;
            int row = 0;

            for (int j = 0; j < board.size(); j++) {
                col += board.get(i, j).numeric();
                row += board.get(j, i).numeric();

            }

            if (Math.abs(col / board.size() - p.numeric()) < 0.01 || Math.abs(row / board.size() - p.numeric()) < 0.01) {
                return true;
            }
        }
        return false;
    }

    private boolean isDraw() {
        int offset = 0;
        for (int i = 0; i < board.size(); i++) {
            if (board.get(board.size() - 1 - i, i).equals(Player.empty())) offset++;
            if (board.get(i, i).equals(Player.empty())) offset++;
            for (int j = 0; j < board.size(); j++) {
                if (board.get(i, j).equals(Player.empty())) offset++;
                if (board.get(j, i).equals(Player.empty())) offset++;
            }
        }
        return offset == 0;
    }

    public synchronized boolean hasEnded() {
        return !isRunning;
    }
}
