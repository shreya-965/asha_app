package com.example.asha_app;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class QuizActivity extends AppCompatActivity {

    ImageView questionImage;
    MediaPlayer mediaPlayer, feedbackPlayer;

    ArrayList<Integer> questionOrder = new ArrayList<>();
    int currentIndex = 0;
    int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionImage = findViewById(R.id.questionImage);
        level = getIntent().getIntExtra("level", 1);
        setupQuestionsForLevel(level);

        loadQuestion(true);

        findViewById(R.id.btnCorrect).setOnClickListener(v -> {
            playRandomFeedbackAudio(reinforcementAudios);
            showToast("Correct");
            nextQuestion();
        });

        findViewById(R.id.btnWrong).setOnClickListener(v -> {
            playRandomFeedbackAudio(redirectionAudios);
            showToast("Wrong");
        });

        findViewById(R.id.btnDisengaged).setOnClickListener(v -> {
            playRandomFeedbackAudio(engageAudios);
            showToast("Disengaged");
        });

        findViewById(R.id.btnSlash).setOnClickListener(v -> {
            handleSlash(level);
            showToast("Slash action");
        });

        findViewById(R.id.btnNext).setOnClickListener(v -> nextQuestion());
        findViewById(R.id.btnPrev).setOnClickListener(v -> previousQuestion());
    }

    void loadQuestion(boolean playAudio) {
        if (currentIndex >= questionOrder.size()) {
            showToast("Quiz Completed");
            return;
        }

        int questionNumber = questionOrder.get(currentIndex);
        String imageName = "img" + questionNumber;
        String audioName = "audio" + questionNumber;

        int imgRes = getResources().getIdentifier(imageName, "drawable", getPackageName());
        int audioRes = getResources().getIdentifier(audioName, "raw", getPackageName());

        if (imgRes != 0) {
            questionImage.setImageResource(imgRes);
        }

        if (mediaPlayer != null) mediaPlayer.release();

        if (playAudio && audioRes != 0) {
            mediaPlayer = MediaPlayer.create(this, audioRes);
            mediaPlayer.start();
        }
    }

    void handleSlash(int level) {
        int questionNumber = questionOrder.get(currentIndex);

        if (level == 1) {
            boolean isGirl = isGirl(questionNumber);
            String init = "this_is";
            String model = isGirl ? "this_is_a_girl" : "this_is_a_boy";

            List<String> choices = Arrays.asList(init, model);
            Collections.shuffle(choices);
            playAudioByName(choices.get(0));

        } else if (level == 2) {
            boolean isGirl = isGirlLevel2Or3(questionNumber);
            String init = isGirl ? "girl_is" : "boy_is";
            String repeat = "repeat" + questionNumber;

            List<String> choices = Arrays.asList(init, repeat);
            Collections.shuffle(choices);
            playAudioByName(choices.get(0));

        } else if (level == 3) {
            boolean isGirl = isGirlLevel2Or3(questionNumber);
            String question = isGirl ? "what_is_the_girl_doing" : "what_is_the_boy_doing";
            String model = "model_" + questionNumber;

            List<String> choices = Arrays.asList(question, model);
            Collections.shuffle(choices);
            playAudioByName(choices.get(0));
        }
    }

    private void playAudioByName(String name) {
        int resId = getResources().getIdentifier(name, "raw", getPackageName());
        if (resId != 0) {
            if (feedbackPlayer != null) feedbackPlayer.release();
            feedbackPlayer = MediaPlayer.create(this, resId);
            feedbackPlayer.start();
        }
    }

    private void playRandomFeedbackAudio(List<Integer> audioList) {
        if (feedbackPlayer != null) feedbackPlayer.release();
        int randomIndex = new Random().nextInt(audioList.size());
        feedbackPlayer = MediaPlayer.create(this, audioList.get(randomIndex));
        feedbackPlayer.start();
    }

    private boolean isGirl(int questionNumber) {
        List<Integer> girls = Arrays.asList(1, 5, 6, 7, 8, 11, 12, 14, 17);
        return girls.contains(questionNumber);
    }

    private boolean isGirlLevel2Or3(int questionNumber) {
        List<Integer> girls = Arrays.asList(22, 23, 25, 27, 29);
        return girls.contains(questionNumber);
    }

    private void setupQuestionsForLevel(int level) {
        questionOrder.clear();
        switch (level) {
            case 1:
                for (int i = 1; i <= 17; i++) questionOrder.add(i);
                break;
            case 2:
            case 3:
                for (int i = 18; i <= 29; i++) questionOrder.add(i);
                break;
            default:
                showToast("Invalid level");
                return;
        }
        Collections.shuffle(questionOrder);
    }

    private void nextQuestion() {
        if (currentIndex < questionOrder.size() - 1) {
            currentIndex++;
            loadQuestion(true);
        } else {
            showToast("End of quiz");
        }
    }

    private void previousQuestion() {
        if (currentIndex > 0) {
            currentIndex--;
            loadQuestion(true);
        } else {
            showToast("Already at first question");
        }
    }

    void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
        new android.os.Handler().postDelayed(toast::cancel, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.release();
        if (feedbackPlayer != null) feedbackPlayer.release();
    }

    List<Integer> reinforcementAudios = Arrays.asList(
            R.raw.reinforce_1, R.raw.reinforce_2, R.raw.reinforce_3, R.raw.reinforce_4, R.raw.reinforce_5
    );

    List<Integer> redirectionAudios = Arrays.asList(
            R.raw.redirect_1, R.raw.redirect_2, R.raw.redirect_3
    );

    List<Integer> engageAudios = Arrays.asList(
            R.raw.engage_1, R.raw.engage_2, R.raw.engage_3, R.raw.engage_4
    );
}
