package nirusu.nirubot.util.tictactoe;

import discord4j.common.util.Snowflake;

public class TicTacToeGame {
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
}
