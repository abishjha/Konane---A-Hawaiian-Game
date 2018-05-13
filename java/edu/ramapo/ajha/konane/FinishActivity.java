package edu.ramapo.ajha.konane;

/*
 ************************************************************
 * Name:     Abish Jha                                      *
 * Project:  Project 1 - Konane                             *
 * Class:    CMPS 331 Artificial Intelligence I             *
 * Date:     February 2, 2018                               *
 ************************************************************
*/

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class FinishActivity extends AppCompatActivity {

    String blackPlayer;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        size = getIntent().getIntExtra("size", 0);
        blackPlayer = getIntent().getStringExtra("blackPlayer");

        displayStats(this.getIntent());
    }


    //
    // parameters :- intent --> the current intent
    // returns    :- (null)
    //
    public void displayStats(Intent intent){
        int player1_s = intent.getIntExtra("player1_score", 0);
        int player2_s = intent.getIntExtra("player2_score", 0);

        String message = "Black got " + player1_s + " points\nWhite got " + player2_s + " points\n\n";

        if(player1_s == player2_s)
            message += " | IT IS A TIE | ";
        else if(player1_s < player2_s)
            message += " | White wins | ";
        else if(player2_s < player1_s)
            message += " | Black wins | ";

        ((TextView) findViewById(R.id.stats_display)).setText(message);
    }


    //
    // parameters :- view --> the current view
    // returns    :- (null)
    //
    public void new_game(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    //
    // parameters :- intent --> the current view
    // returns    :- (null)
    //
    public void restart(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("game", "1"); // means a new game
        intent.putExtra("blackPlayer", blackPlayer);
        intent.putExtra("size", size);
        startActivity(intent);
    }
}
