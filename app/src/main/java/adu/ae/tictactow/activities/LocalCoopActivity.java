package adu.ae.tictactow.activities;

import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import adu.ae.tictactow.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class LocalCoopActivity extends AppCompatActivity {
    private static String localActivityTag = "LocalCoop Activity";

    private Button[][] board = new Button[3][3];

    private int gameCount, player1Points,player2Points;

    private TextView topTextView;
    private TextView p1RoundResultTextView, p2RoundResultTextView;
    private TextView player1PointsTextView, player2PointsTextView;
    private TextView playerWinnerTextView;
    private TextView matchEndedTextView;
    private TextView nameTag1TextView, nameTag2TextView;

    private CircleImageView player1CircularImageView, player2CircularImageView;

    private boolean player1FirstTurn= true, player1Turn = true;

    private ArrayList<ArrayList<Button>> winningCombinationsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_coop);

        setImmersiveMode();
        setViews();
        createBoard();
        setWinningCombinations();

    }

    private void setImmersiveMode(){

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }


    private void setViews(){
        player1PointsTextView = findViewById(R.id.p1PointsTextView);
        player2PointsTextView = findViewById(R.id.p2PointsTextView);
        p1RoundResultTextView = findViewById(R.id.p1RoundResultTextView);
        p2RoundResultTextView = findViewById(R.id.p2RoundResultTextView);
        topTextView = findViewById(R.id.topText);
        topTextView.setText("Player 1's Turn");

        nameTag1TextView = findViewById(R.id.name_tag1);
        nameTag2TextView = findViewById(R.id.name_tag2);
        nameTag1TextView.setText("Player1");
        nameTag2TextView.setText("Player2");

        player1CircularImageView = findViewById(R.id.player1CircularImageView);
        player2CircularImageView = findViewById(R.id.player2CircularImageView);

        playerWinnerTextView = findViewById(R.id.playerWinnerTextView);
        matchEndedTextView = findViewById(R.id.matchEndedTextView);



    }

    private void createBoard(){
        for(int i=0; i< board.length; i++){
            for(int j=0; j< board[i].length;j++){
                String buttonId = "button_" +i+j;
                board[i][j] = findViewById(getResources().getIdentifier(buttonId, "id", getPackageName()));
                board[i][j].setText("");
                board[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!((Button) view).getText().toString().equals("")){
                            return;
                        }

                        gameCount++;


                        if(player1FirstTurn){
                            if(player1Turn){
                                ((Button) view).setTextColor(ContextCompat.getColor(LocalCoopActivity.this,R.color.reddish_pink));
                                ((Button) view).setText("X");
                                topTextView.setText("Player 2's Turn");
                            } else{
                                ((Button) view).setTextColor(ContextCompat.getColor(LocalCoopActivity.this,R.color.light_violet));
                                ((Button) view).setText("O");
                                topTextView.setText("Player 1's Turn");
                            }

                        } else{
                            if(player1Turn){
                                ((Button) view).setTextColor(ContextCompat.getColor(LocalCoopActivity.this,R.color.light_violet));
                                ((Button) view).setText("O");
                                topTextView.setText("Player 2's Turn");
                            } else{
                                ((Button) view).setTextColor(ContextCompat.getColor(LocalCoopActivity.this,R.color.reddish_pink));
                                ((Button) view).setText("X");
                                topTextView.setText("Player 1's Turn");
                            }
                        }


                        if(checkWin()){

                            if(player1Turn){
                                player1Wins();
                            } else{
                                player2Wins();
                            }

                            matchEnded();
                            return;
                        } else if(gameCount ==9){

                            draw();
                            matchEnded();
                            return;
                        }

                        player1Turn = !player1Turn;


                    }
                });
            }
        }

    }




    private void setWinningCombinations(){

        // for rows
        for(int i=0; i< board.length; i++){
            ArrayList<Button> rowWinningCombination = new ArrayList<>();

            for (int j=0; j< board[i].length;j++){
                rowWinningCombination.add(board[i][j]);
            }
            winningCombinationsList.add(rowWinningCombination);
        }


        //for columns
        for(int i=0; i< board.length; i++){
            ArrayList<Button> columnWinningCombination = new ArrayList<>();

            for (int j=0; j< board[i].length;j++){
                columnWinningCombination.add(board[j][i]);
            }
            winningCombinationsList.add(columnWinningCombination);
        }

        //for diagonals
        ArrayList<Button> diagonalWinningCombination = new ArrayList<>();
        diagonalWinningCombination.add(board[0][0]);
        diagonalWinningCombination.add(board[1][1]);
        diagonalWinningCombination.add(board[2][2]);
        winningCombinationsList.add(diagonalWinningCombination);

        diagonalWinningCombination = new ArrayList<>();
        diagonalWinningCombination.add(board[0][2]);
        diagonalWinningCombination.add(board[1][1]);
        diagonalWinningCombination.add(board[2][0]);
        winningCombinationsList.add(diagonalWinningCombination);
    }

    private boolean checkWin(){

        for(ArrayList<Button> winningCombination : winningCombinationsList){


            if(!winningCombination.get(0).getText().equals("") &&
                    winningCombination.get(0).getText().equals(winningCombination.get(1).getText()) &&
                    winningCombination.get(0).getText().equals(winningCombination.get(2).getText())){



                return true;
            }
        }

        return false;
    }


    private void player1Wins(){
        player1Points++;
        updatePointsText();
        playerWinnerTextView.setText("Player 1 WIns!");
        p1RoundResultTextView.setText("WIN!");
    }

    private void player2Wins(){
        player2Points++;
        updatePointsText();
        playerWinnerTextView.setText("Player 2 WIns!");
        p2RoundResultTextView.setText("WIN!");
    }


    private void draw(){
        playerWinnerTextView.setText("DRAW!");
        p1RoundResultTextView.setText("DRAW");
        p2RoundResultTextView.setText("DRAW");

    }

    private void reset(){

        gameCount = 0;
        player1FirstTurn = !player1FirstTurn;
        player1Turn = player1FirstTurn;
        createBoard();
        topTextView.setText(player1Turn? "Player 1's Turn" : "Player 2's Turn");
        matchEndedTextView.setText(null);
        playerWinnerTextView.setText(null);
        p1RoundResultTextView.setText(null);
        p2RoundResultTextView.setText(null);
        setDisabledBoard(false);

        if(player1FirstTurn){
            changeP1ReddishPink();
            changeP2LightViolet();
        } else{
            changeP1LightViolet();
            changeP2ReddishPink();
        }


    }

    private void updatePointsText(){
        player1PointsTextView.setText(String.valueOf(player1Points));
        player2PointsTextView.setText(String.valueOf(player2Points));
    }

    private void matchEnded(){
        matchEndedTextView.setText("Match Ended");
        setDisabledBoard(true);

        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                topTextView.setText("Next round begins in " + (millisUntilFinished / 1000) + "...");

            }

            public void onFinish() {
                reset();
            }
        }.start();
    }

    private void setDisabledBoard(Boolean bool){

        for(int i=0; i< board.length; i++){
            for(int j=0; j< board[i].length;j++){
                board[i][j].setClickable(!bool);
            }
        }
    }

    private void changeP1ReddishPink(){
        player1CircularImageView.setBorderColor(ContextCompat.getColor(this, R.color.reddish_pink));
        nameTag1TextView.setBackground(ContextCompat.getDrawable(this,R.drawable.name_tag_reddish_pink));
    }

    private void changeP1LightViolet(){
        player1CircularImageView.setBorderColor(ContextCompat.getColor(this, R.color.light_violet));
        nameTag1TextView.setBackground(ContextCompat.getDrawable(this,R.drawable.name_tag_light_violet));
    }

    private void changeP2ReddishPink(){
        player2CircularImageView.setBorderColor(ContextCompat.getColor(this, R.color.reddish_pink));
        nameTag2TextView.setBackground(ContextCompat.getDrawable(this,R.drawable.name_tag_reddish_pink));
    }

    private void changeP2LightViolet(){
        player2CircularImageView.setBorderColor(ContextCompat.getColor(this, R.color.light_violet));
        nameTag2TextView.setBackground(ContextCompat.getDrawable(this,R.drawable.name_tag_light_violet));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("player1Points",player1Points);
        outState.putInt("player2Points",player2Points);
        outState.putInt("gameCount",gameCount);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        savedInstanceState.getInt("player1Points");
        savedInstanceState.getInt("player2Points");
        savedInstanceState.getInt("gameCount");
    }
}

