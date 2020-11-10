package adu.ae.tictactow.activities;

import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import adu.ae.tictactow.utils.BoardIndex;
import adu.ae.tictactow.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class SinglePlayerActivity extends AppCompatActivity {
    private static String singleActivityTag = "SinglePlayer Activity";

    private Button[][] board = new Button[3][3];

    private int gameCount,humanPlayerPoints,aiPlayer2Points;

    private TextView topTextView;
    private TextView humanPlayerRoundResultTextView, aiPlayerRoundResultTextView;
    private TextView humanPlayerPointsTextView,aiPlayerPointsTextView;
    private TextView playerWinnerTextView;
    private TextView matchEndedTextView;
    private TextView nameTag1TextView, nameTag2TextView;

    private CircleImageView player1CircularImageView, player2CircularImageView;


    private boolean humanPlayerTurn = true;
    private boolean humanPlayerFirstTurn = true;

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
        humanPlayerPointsTextView = findViewById(R.id.p1PointsTextView);
        aiPlayerPointsTextView = findViewById(R.id.p2PointsTextView);
        humanPlayerRoundResultTextView = findViewById(R.id.p1RoundResultTextView);
        aiPlayerRoundResultTextView = findViewById(R.id.p2RoundResultTextView);
        topTextView = findViewById(R.id.topText);
        topTextView.setText("Player 1's Turn");

        nameTag1TextView = findViewById(R.id.name_tag1);
        nameTag2TextView = findViewById(R.id.name_tag2);
        nameTag1TextView.setText("Player1");
        nameTag2TextView.setText("CompAI");

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




                        if(humanPlayerFirstTurn){
                            ((Button) view).setTextColor(ContextCompat.getColor(SinglePlayerActivity.this,R.color.reddish_pink));
                            ((Button) view).setText("X");

                        } else{
                            ((Button) view).setTextColor(ContextCompat.getColor(SinglePlayerActivity.this,R.color.light_violet));
                            ((Button) view).setText("O");
                        }



                        if(checkWin()){

                            humanPlayerWins();
                            matchEnded();
                            return;

                        } else if(gameCount ==9){

                            draw();
                            matchEnded();
                            return;
                        }

                        humanPlayerTurn = !humanPlayerTurn;
                        topTextView.setText("Comp is choosing...");


                        aiPlayerPlays();



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


    private void humanPlayerWins(){
        humanPlayerPoints++;
        updatePointsText();
        playerWinnerTextView.setText("Player 1 WIns!");
        humanPlayerRoundResultTextView.setText("WIN!");
    }

    private void aiPlayerWins(){
        aiPlayer2Points++;
        updatePointsText();
        playerWinnerTextView.setText("Comp WIns!");
        aiPlayerRoundResultTextView.setText("WIN!");
    }


    private void draw(){
        playerWinnerTextView.setText("DRAW!");
        humanPlayerRoundResultTextView.setText("DRAW");
        aiPlayerRoundResultTextView.setText("DRAW");

    }

    private void reset(){
        gameCount = 0;
        humanPlayerTurn = !humanPlayerTurn;
        humanPlayerFirstTurn = !humanPlayerFirstTurn;
        createBoard();
        topTextView.setText(humanPlayerFirstTurn ? "Player 1's Turn" : "Comp is choosing...");
        matchEndedTextView.setText(null);
        playerWinnerTextView.setText(null);
        humanPlayerRoundResultTextView.setText(null);
        aiPlayerRoundResultTextView.setText(null);
        if(humanPlayerFirstTurn){
            changeP1ReddishPink();
            changeP2LightViolet();
        }
        if(!humanPlayerFirstTurn){
            changeP1LightViolet();
            changeP2ReddishPink();
            aiPlayerPlays();
        }

    }

    private void updatePointsText(){
        humanPlayerPointsTextView.setText(String.valueOf(humanPlayerPoints));
        aiPlayerPointsTextView.setText(String.valueOf(aiPlayer2Points));
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


    private ArrayList<BoardIndex> getAvailableSlots(){

        ArrayList<BoardIndex> availableSlots = new ArrayList<>();

        for(int i=0; i< board.length; i++){
            for(int j=0; j< board[i].length;j++){

                if(board[i][j].getText() == "" || board[i][j].getText() == null){
                    availableSlots.add(new BoardIndex(i,j));
                }
            }
        }

        return availableSlots;
    }


    private void aiPlayerPlays(){
        setDisabledBoard(true);

        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {}

            public void onFinish() {

                ArrayList<BoardIndex> availableSlots = getAvailableSlots();
                Random random = new Random();


                int randomInt;
                if(availableSlots.size()>1){
                    randomInt = random.nextInt((availableSlots.size()-1));
                } else{
                    randomInt=0;
                }


                int row =  availableSlots.get(randomInt).getRow();
                int column = availableSlots.get(randomInt).getColumn();

                Button button = board[row][column];



                if(humanPlayerFirstTurn){
                    button.setTextColor(ContextCompat.getColor(SinglePlayerActivity.this,R.color.light_violet));
                    button.setText("O");

                } else{
                    button.setTextColor(ContextCompat.getColor(SinglePlayerActivity.this,R.color.reddish_pink));
                    button.setText("X");
                }
                gameCount++;

                System.out.println("GAMAE COUNT " + gameCount);


                if(checkWin()){

                    aiPlayerWins();
                    matchEnded();
                    return;

                } else if(gameCount ==9){

                    draw();
                    matchEnded();
                    return;
                }

                topTextView.setText("Player 1's Turn");
                humanPlayerTurn = !humanPlayerTurn;

                setDisabledBoard(false);

            }
        }.start();

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

        outState.putInt("humanPlayerPoints", humanPlayerPoints);
        outState.putInt("aiPlayer2Points", aiPlayer2Points);
        outState.putInt("gameCount",gameCount);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        savedInstanceState.getInt("humanPlayerPoints");
        savedInstanceState.getInt("aiPlayer2Points");
        savedInstanceState.getInt("gameCount");
    }
}
