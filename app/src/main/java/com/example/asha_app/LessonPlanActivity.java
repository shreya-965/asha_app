package com.example.asha_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LessonPlanActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_plan);

        Button option1 = findViewById(R.id.option1);
        Button option2 = findViewById(R.id.option2);
        Button option3 = findViewById(R.id.option3);

        option1.setOnClickListener(v -> Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show());
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LessonPlanActivity.this, LevelSelectActivity.class);
                intent.putExtra("mode", "wordToPhrase"); // optional, remove if you don’t use it
                startActivity(intent);
            }
        });

        option3.setOnClickListener(v -> Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show());
    }
}
