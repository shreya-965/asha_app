package com.example.asha_app;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LevelSelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        View.OnClickListener listener = v -> {
            int level = Integer.parseInt(((Button) v).getText().toString().replace("Level ", ""));
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("level", level);
            intent.putExtra("mode", getIntent().getStringExtra("mode"));
            startActivity(intent);
        };

        findViewById(R.id.btnLevel1).setOnClickListener(listener);
        findViewById(R.id.btnLevel2).setOnClickListener(listener);
        findViewById(R.id.btnLevel3).setOnClickListener(listener);
    }
}
