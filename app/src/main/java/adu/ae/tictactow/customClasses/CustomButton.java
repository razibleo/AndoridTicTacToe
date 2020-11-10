package adu.ae.tictactow.customClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatButton;

import adu.ae.tictactow.utils.BoardIndex;

public class CustomButton extends AppCompatButton {

    private BoardIndex boardIndex;

    public CustomButton(Context context) {
        super(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBoardIndex(BoardIndex boardIndex) {
        this.boardIndex = boardIndex;
    }

    public BoardIndex getBoardIndex() {
        return boardIndex;
    }
}
