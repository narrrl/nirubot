package nirusu.nirubot.util.tictactoe;

import discord4j.common.util.Snowflake;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TicTacToeGame {
    /**
     * This HashMap stores the player and the game. Stores Two instances per game.
     */
    public static final Map<Snowflake, TicTacToeGame> games = new HashMap<>();

    private Snowflake playerOne;
    private Snowflake playerTwo;
    private Snowflake playerToMove;
    private TTTBoard board;

    public TicTacToeGame(Snowflake playerOne, Snowflake playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;

        //player one always starts
        this.playerToMove = playerOne;
        this.board = new TTTBoard();
    }

    public Snowflake getPlayerOne() {
        return playerOne;
    }

    public Snowflake getPlayerTwo() {
        return playerTwo;
    }

    public Snowflake getPlayerToMove() {
        return playerToMove;
    }

    public TTTBoard getBoard() {
        return board;
    }

    //switch the next player to move
    public void advanceUser() {
        if (this.playerToMove.equals(this.getPlayerOne())) {
            this.playerToMove = this.playerTwo;
            return;
        }

        if (this.playerToMove.equals(this.getPlayerTwo())) {
            this.playerToMove = this.playerOne;
        }
    }

    public Optional<Snowflake> getWinner() {
        int rowCounter = 0;
        int colCounter = 0;
        int mainDiagonalCounter = 0;
        int secondaryDiagonalCounter = 0;

        //sum over rows and cols
        for (int y = 0; y < TTTBoard.BOARD_SIZE; y++) {
            for (int x = 0; x < TTTBoard.BOARD_SIZE; x++) {
                rowCounter += this.getBoard().getArray()[y][x];
                colCounter += this.getBoard().getArray()[x][y];
            }
            //increment diagonal counters
            mainDiagonalCounter += this.getBoard().getArray()[y][y];
            secondaryDiagonalCounter += this.getBoard()
                    .getArray()[TTTBoard.BOARD_SIZE - y - 1][TTTBoard.BOARD_SIZE - y - 1];

            //check if player one won
            if (rowCounter == TTTBoard.BOARD_SIZE * TTTBoard.PLAYER_ONE
                    || colCounter == TTTBoard.BOARD_SIZE * TTTBoard.PLAYER_ONE
                    || mainDiagonalCounter == TTTBoard.BOARD_SIZE * TTTBoard.PLAYER_ONE
                    || secondaryDiagonalCounter == TTTBoard.BOARD_SIZE * TTTBoard.PLAYER_ONE) {
                return Optional.of(this.playerOne);
            }

            //check if player two won
            if (rowCounter == TTTBoard.BOARD_SIZE * TTTBoard.PLAYER_TWO
                    || colCounter == TTTBoard.BOARD_SIZE * TTTBoard.PLAYER_TWO
                    || mainDiagonalCounter == TTTBoard.BOARD_SIZE * TTTBoard.PLAYER_TWO
                    || secondaryDiagonalCounter == TTTBoard.BOARD_SIZE * TTTBoard.PLAYER_TWO) {
                return Optional.of(this.playerTwo);
            }
            //reset counters
            rowCounter = 0;
            colCounter = 0;
        }

        //nobody won
        return Optional.empty();
    }

    public int evalPlayer(Snowflake player) {
        if (player.equals(this.getPlayerOne())) return TTTBoard.PLAYER_ONE;
        if (player.equals(this.getPlayerTwo())) return TTTBoard.PLAYER_TWO;
        return TTTBoard.NOPLAYER;
    }
}
