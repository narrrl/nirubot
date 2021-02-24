package nirusu.nirubot.model.tictactoe;

import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class TicTacToe {
    private static final String WRONG_PLAYER = "This is not your Turn!";
    private static final String CANNOT_PLACE = "You can't place here";
    private static final String PLAYER_WON = "Player {} won the game!";
    private static final String DRAW = "Draw!";
    private static final float EPSILON = 1.0E-4f;
    private final Player one;
    private final Player two;
    private final TicTacToeBoard board;
    private Player current;
    private boolean isRunning;

    public TicTacToe(Player one, Player two, int boardSize) {
        this.one = one;
        this.two = two;

        // player one always starts
        this.current = one;

        this.board = new TicTacToeBoard(boardSize);
        isRunning = true;
    }

    public synchronized String surrender(Player p) {

        this.isRunning = false;

        if (p.equals(one)) {
            return board.toString() + "\n\n" + PLAYER_WON.replace("{}", two.toString());
        }
        return board.toString() + "\n\n" + PLAYER_WON.replace("{}", one.toString());
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
                return board.toString() + "\n\n" + PLAYER_WON.replace("{}", p.toString());
            }
        }

        if (!canWin(one) && !canWin(two)) {
            isRunning = false;
            return board.toString() + "\n\n" + DRAW;
        }

        advanceUser();

        return board.toString();
    }

    // switch the next player to move
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

            if (Math.abs(dia / board.size() - p.numeric()) < EPSILON
                    || Math.abs(secDia / board.size() - p.numeric()) < EPSILON) {
                return true;
            }

            int col = 0;
            int row = 0;

            for (int j = 0; j < board.size(); j++) {
                col += board.get(i, j).numeric();
                row += board.get(j, i).numeric();

            }

            if (Math.abs(col / board.size() - p.numeric()) < EPSILON
                    || Math.abs(row / board.size() - p.numeric()) < EPSILON) {
                return true;
            }
        }
        return false;
    }

    private boolean canWin(Player p) {
        Set<Player> dia = new HashSet<>();
        Set<Player> secDia = new HashSet<>();
        Player o = getOtherPlayer(p);
        for (int i = 0; i < board.size(); i++) {

            dia.add(board.get(i, i));
            secDia.add(board.get(board.size() - 1 - i, i));

            if (i == board.size() - 1 && (!dia.contains(o) || !secDia.contains(o))) {
                return true;
            }

            Set<Player> col = new HashSet<>();
            Set<Player> row = new HashSet<>();

            for (int j = 0; j < board.size(); j++) {
                col.add(board.get(i, j));
                row.add(board.get(j, i));

            }

            if (!col.contains(o) || !row.contains(o)) {
                return true;
            }
        }
        return false;
    }

    private Player getOtherPlayer(Player p) {
        if (p.equals(one)) {
            return two;
        }
        return one;
    }

    public synchronized boolean hasEnded() {
        return !isRunning;
    }
}
