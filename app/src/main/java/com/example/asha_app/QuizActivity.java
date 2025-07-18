package com.example.asha_app;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {
    int[] questionOrder = {1, 2, 3}; // Sample question numbers
    int currentIndex = 0;

    ImageView questionImage;
    MediaPlayer mediaPlayer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionImage = findViewById(R.id.questionImage);
        loadQuestion();

        findViewById(R.id.btnNext).setOnClickListener(v -> {
            if (currentIndex < questionOrder.length - 1) {
                currentIndex++;
                loadQuestion();
            } else {
                showToast("No more questions");
            }
        });

        findViewById(R.id.btnPrev).setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                loadQuestion();
            } else {
                showToast("This is the first question");
            }
        });

        findViewById(R.id.btnCorrect).setOnClickListener(v -> showToast("Correct"));
        findViewById(R.id.btnWrong).setOnClickListener(v -> showToast("Wrong"));
        findViewById(R.id.btnSkip).setOnClickListener(v -> showToast("Skipped"));
        findViewById(R.id.btnDisengaged).setOnClickListener(v -> showToast("Disengaged"));
    }

    void loadQuestion() {
        int questionNumber = questionOrder[currentIndex];

        int imgRes = getResources().getIdentifier("wordtophrase_" + questionNumber, "drawable", getPackageName());
        int audioRes = getResources().getIdentifier("audio" + questionNumber, "raw", getPackageName());

        if (imgRes != 0) {
            questionImage.setImageResource(imgRes);
        } else {
            showToast("Image not found for question " + questionNumber);
        }

        if (mediaPlayer != null) mediaPlayer.release();

        if (audioRes != 0) {
            mediaPlayer = MediaPlayer.create(this, audioRes);
            mediaPlayer.start();
        } else {
            showToast("Audio not found for question " + questionNumber);
        }
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
