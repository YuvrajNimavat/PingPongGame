package com.example.mypingpong;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private PongGame pongGame;
    private PongGame1v1 pongGame1v1;
    private Button computerButton, oneVOneButton;
    private FrameLayout gameContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameContainer = findViewById(R.id.game_container);
        computerButton = findViewById(R.id.computer_button);
        oneVOneButton = findViewById(R.id.one_v_one_button);

        computerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startComputerMode();
            }
        });

        oneVOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start1v1Mode();
            }
        });
    }

    private void startComputerMode() {
        gameContainer.removeAllViews();
        pongGame = new PongGame(this);
        gameContainer.addView(pongGame);
        Toast.makeText(this, "Computer mode started", Toast.LENGTH_SHORT).show();
    }

    private void start1v1Mode() {
        gameContainer.removeAllViews();
        pongGame1v1 = new PongGame1v1(this);
        gameContainer.addView(pongGame1v1);
        Toast.makeText(this, "1v1 mode started", Toast.LENGTH_SHORT).show();
    }

}
