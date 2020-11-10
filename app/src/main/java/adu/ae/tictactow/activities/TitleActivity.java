package adu.ae.tictactow.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import adu.ae.tictactow.R;
import adu.ae.tictactow.customClasses.FriendAdapter;
import adu.ae.tictactow.utils.Friend;

public class TitleActivity extends AppCompatActivity {

    private ArrayList<Friend> friendArrayList = new ArrayList<>();
    private RecyclerView friendRecyclerView;
    private ImageView leftArrowImageView;
    private ImageView rightArrowImageView;
    private Button localCoopButton;
    private Button singlePlayerButton;
    private Button multiPlayerButton;
    private Button settingsButton;


    private RecyclerView.Adapter friendAdapter;
    private RecyclerView.LayoutManager friendLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);


        setImmersiveMode();
        setViews();
        addFriends();
        createRecyclerView();
        addArrowListeners();
        addButtonListeners();



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
        friendRecyclerView = findViewById(R.id.friendsRecyclerView);
        leftArrowImageView = findViewById(R.id.leftArrow);
        rightArrowImageView = findViewById(R.id.rightArrow);

        localCoopButton = findViewById(R.id.localCoopButton);
        singlePlayerButton = findViewById(R.id.singlePlayerButton);
        multiPlayerButton = findViewById(R.id.multiplayerButton);
        settingsButton = findViewById(R.id.settingsButton);
    }


    private void addFriends(){
        friendArrayList.add(new Friend(R.drawable.grandma));
        friendArrayList.add(new Friend(R.drawable.stock_man_1));
        friendArrayList.add(new Friend(R.drawable.stock_man_2));
        friendArrayList.add(new Friend(R.drawable.stock_grandma_2));
        friendArrayList.add(new Friend(R.drawable.grandma));
        friendArrayList.add(new Friend(R.drawable.stock_man_1));
        friendArrayList.add(new Friend(R.drawable.stock_man_2));
        friendArrayList.add(new Friend(R.drawable.stock_grandma_2));

    }

    private void createRecyclerView(){

        friendAdapter = new FriendAdapter(friendArrayList);
        friendLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        friendRecyclerView.setAdapter(friendAdapter);
        friendRecyclerView.setHasFixedSize(true);
        friendRecyclerView.setLayoutManager(friendLayoutManager);
    }



    private void addArrowListeners(){

        leftArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendRecyclerView.scrollBy(-100,0);
            }
        });

        rightArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendRecyclerView.scrollBy(100,0);
            }
        });
    }


    private void addButtonListeners(){

        localCoopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TitleActivity.this, LocalCoopActivity.class);
                startActivity(intent);

            }
        });

        singlePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TitleActivity.this, SinglePlayerActivity.class);
                startActivity(intent);

            }
        });


        multiPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TitleActivity.this, BluetoothActivity.class);
                startActivity(intent);

            }
        });


        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setImmersiveMode();
    }
}
