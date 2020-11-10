package adu.ae.tictactow.activities;

import android.bluetooth.BluetoothSocket;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


import adu.ae.tictactow.utils.BoardIndex;
import adu.ae.tictactow.customClasses.CustomButton;
import adu.ae.tictactow.utils.PlayerMove;
import adu.ae.tictactow.R;
import adu.ae.tictactow.multiplayerThreads.ReadWriteThread;
import de.hdodenhof.circleimageview.CircleImageView;

public class LocalMultiplayerActivity extends AppCompatActivity {

    private Handler handler;
    public static BluetoothSocket mmSocket;
    private ReadWriteThread readWriteThread;


    private static String localMultiplayerActivityTag = "LocalMultiplayer Activity";

    private Button[][] board = new Button[3][3];

    private int gameCount, player1Points, player2Points;

    private TextView topTextView;
    private TextView player1RoundResultTextView,player2RoundResultTextView;
    private TextView player1PointsTextView, player2PointsTextView;
    private TextView playerWinnerTextView;
    private TextView matchEndedTextView;
    private TextView nameTag1TextView, nameTag2TextView;

    private CircleImageView player1CircularImageView, player2CircularImageView;


    public static boolean player1Turn;
    public static boolean player1FirstTurn;

    private ArrayList<ArrayList<Button>> winningCombinationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_coop);


        setImmersiveMode();


        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {


                    switch(message.what){

                        case ReadWriteThread.MessageConstants.MESSAGE_READ:
                            byte[] bytes = (byte[])message.obj;


                            try {
                                PlayerMove playerMove = (PlayerMove)deserialize(bytes);
                                if(playerMove.getSymbol()!=null){player2Plays(playerMove);}
                                else{
                                    Toast.makeText(LocalMultiplayerActivity.this, "Player Disconnected", Toast.LENGTH_SHORT).show();
                                    finish();}

                            } catch (IOException e) {
                                Log.e(localMultiplayerActivityTag,"Io exception at deserialize");
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                Log.e(localMultiplayerActivityTag,"Class Not Found exception at deserialize");
                                e.printStackTrace();
                            }
                            break;

                        case ReadWriteThread.MessageConstants.MESSAGE_WRITE:
                            break;

                        case ReadWriteThread.MessageConstants.MESSAGE_TOAST:
                            break;

                    }


                return false;
            }
        });


        setViews();
        createBoard();
        setWinningCombinations();


        if(player1FirstTurn){
            setDisabledBoard(false);
        } else{
            setDisabledBoard(true);
        }

        readWriteThread = new ReadWriteThread(mmSocket,handler);
        readWriteThread.start();



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
        player1RoundResultTextView = findViewById(R.id.p1RoundResultTextView);
        player2RoundResultTextView = findViewById(R.id.p2RoundResultTextView);
        topTextView = findViewById(R.id.topText);


        nameTag1TextView = findViewById(R.id.name_tag1);
        nameTag2TextView = findViewById(R.id.name_tag2);
        nameTag1TextView.setText("Player1");
        nameTag2TextView.setText("Player2");

        player1CircularImageView = findViewById(R.id.player1CircularImageView);
        player2CircularImageView = findViewById(R.id.player2CircularImageView);

        playerWinnerTextView = findViewById(R.id.playerWinnerTextView);
        matchEndedTextView = findViewById(R.id.matchEndedTextView);

        if(player1FirstTurn){
            topTextView.setText("Player 1's Turn");
            changeP1ReddishPink();
            changeP2LightViolet();

        } else{
            topTextView.setText("Player 2's Turn");
            changeP1LightViolet();
            changeP2ReddishPink();
        }

    }


    private void createBoard(){
        for(int i=0; i< board.length; i++){
            for(int j=0; j< board[i].length;j++){
                String buttonId = "button_" +i+j;
                board[i][j] = findViewById(getResources().getIdentifier(buttonId, "id", getPackageName()));
                board[i][j].setText("");
                ((CustomButton)board[i][j]).setBoardIndex(new BoardIndex(i,j));
                board[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!((Button) view).getText().toString().equals("")){
                            return;
                        }

                        gameCount++;


                        String symbol;


                        if(player1FirstTurn){
                            ((Button) view).setTextColor(ContextCompat.getColor(LocalMultiplayerActivity.this,R.color.reddish_pink));
                            symbol ="X";
                            ((Button) view).setText(symbol);


                        } else{
                            ((Button) view).setTextColor(ContextCompat.getColor(LocalMultiplayerActivity.this,R.color.light_violet));
                            symbol ="O";
                            ((Button) view).setText(symbol);
                        }


                        PlayerMove playerMove = new PlayerMove(symbol, ((CustomButton) view).getBoardIndex());

                        try {
                            byte[] bytes = serialize(playerMove);
                            readWriteThread.write(bytes);
                        } catch (IOException e) {
                            Log.e(localMultiplayerActivityTag, " Error occurred when serializing");
                            e.printStackTrace();
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

                        player1Turn = !player1Turn;
                        topTextView.setText("Player2 is choosing...");

                        setDisabledBoard(true); //Player 2 Plays



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
        player1Points++;
        updatePointsText();
        playerWinnerTextView.setText("Player 1 WIns!");
        player1RoundResultTextView.setText("WIN!");
    }



    private void player2Wins(){
        player2Points++;
        updatePointsText();
        playerWinnerTextView.setText("Player 2 WIns!");
        player2RoundResultTextView.setText("WIN!");

    }

    private void draw(){
        playerWinnerTextView.setText("DRAW!");
        player1RoundResultTextView.setText("DRAW");
        player2RoundResultTextView.setText("DRAW");

    }

    private void reset(){
        gameCount = 0;
        player1Turn = !player1Turn;
        player1FirstTurn = !player1FirstTurn;
        createBoard();
        topTextView.setText(player1FirstTurn ? "Player 1's Turn" : "Player2 is choosing...");
        matchEndedTextView.setText(null);
        playerWinnerTextView.setText(null);
        player1RoundResultTextView.setText(null);
        player2RoundResultTextView.setText(null);
        if(player1FirstTurn){
            changeP1ReddishPink();
            changeP2LightViolet();
        }
        else {
            changeP1LightViolet();
            changeP2ReddishPink();

            setDisabledBoard(true);
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




    private void player2Plays(PlayerMove playerMove){

        String symbol = playerMove.getSymbol();
        int column = playerMove.getBoardIndex().getColumn();
        int row = playerMove.getBoardIndex().getRow();

        Button button = board[row][column];

        if(player1FirstTurn){
            button.setTextColor(ContextCompat.getColor(LocalMultiplayerActivity.this,R.color.light_violet));

        } else{
            button.setTextColor(ContextCompat.getColor(LocalMultiplayerActivity.this,R.color.reddish_pink));
        }

        button.setText(symbol);
        gameCount++;


        if(checkWin()){

            player2Wins();
            matchEnded();
            return;

        } else if(gameCount ==9){

            draw();
            matchEnded();
            return;
        }

        topTextView.setText("Player 1's Turn");
        player1Turn = !player1Turn;

        setDisabledBoard(false);

    }



    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
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

        outState.putInt("player1Points", player1Points);
        outState.putInt("player2Points", player2Points);
        outState.putInt("gameCount",gameCount);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        savedInstanceState.getInt("player1Points");
        savedInstanceState.getInt("player2Points");
        savedInstanceState.getInt("gameCount");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "Local onDestroy", Toast.LENGTH_SHORT).show();

        //smth weird
        // i think restart happens fist then onDestroy

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        PlayerMove playerMove = new PlayerMove(null, null);

        try {
            byte[] bytes = serialize(playerMove);
            readWriteThread.cancel(bytes);
        } catch (IOException e) {
            readWriteThread.cancel();
            Log.e(localMultiplayerActivityTag, " Error occurred when serializing");
            e.printStackTrace();
        }
    }


}
