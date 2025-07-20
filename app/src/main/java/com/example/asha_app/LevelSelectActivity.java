package com.example.asha_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LevelSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        // Get mode from intent (default to "wordToPhrase" if null)
        final String modeRaw = getIntent().getStringExtra("mode");
        final String mode = (modeRaw != null) ? modeRaw : "wordToPhrase";

        // Debug toast
        Toast.makeText(this, "Mode: " + mode, Toast.LENGTH_SHORT).show();

        // Common click listener for level buttons
        View.OnClickListener levelClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = ((Button) v).getText().toString().trim();
                int level = 1; // Default fallback

                if (buttonText.contains("1")) {
                    level = 1;
                } else if (buttonText.contains("2")) {
                    level = 2;
                } else if (buttonText.contains("3")) {
                    level = 3;
                }

                Log.d("LevelSelect", "Selected level: " + level + ", mode: " + mode);

                Intent intent = new Intent(LevelSelectActivity.this, QuizActivity.class);
                intent.putExtra("level", level);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        };

        // Assign listeners to level buttons
        findViewById(R.id.btnLevel1).setOnClickListener(levelClickListener);
        findViewById(R.id.btnLevel2).setOnClickListener(levelClickListener);
        findViewById(R.id.btnLevel3).setOnClickListener(levelClickListener);
    }
}
