package adu.ae.tictactow.utils;

import java.io.Serializable;

public class PlayerMove implements Serializable {
    private String symbol;
    private BoardIndex boardIndex;

    public PlayerMove(String symbol, BoardIndex boardIndex) {
        this.symbol = symbol;
        this.boardIndex = boardIndex;
    }

    public String getSymbol() {
        return symbol;
    }

    public BoardIndex getBoardIndex() {
        return boardIndex;
    }
}
