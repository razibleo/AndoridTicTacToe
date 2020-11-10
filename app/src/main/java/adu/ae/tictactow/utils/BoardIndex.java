package adu.ae.tictactow.utils;

import java.io.Serializable;

public class BoardIndex implements Serializable {

    private int row;
    private int column;

    public BoardIndex(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
