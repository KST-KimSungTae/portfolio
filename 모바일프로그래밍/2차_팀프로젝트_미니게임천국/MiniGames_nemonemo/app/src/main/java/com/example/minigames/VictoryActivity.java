package com.example.minigames;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class VictoryActivity extends AppCompatActivity {
    private TextView winnerText;
    private Button mainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victory);

        winnerText = findViewById(R.id.winnerText);
        mainButton = findViewById(R.id.mainButton);

        Intent intent = getIntent();
        String winner = intent.getStringExtra("winner");
        winnerText.setText(winner + "이(가) 승리했습니다!");

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
