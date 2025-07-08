package com.quizapp.example.ui.page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.quizapp.example.R;
import com.quizapp.example.model.QuizData;
import com.quizapp.example.ui.FinalResultActivity;
import com.quizapp.example.util.Constants;
import com.quizapp.example.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeographyOrLiteratureQuizActivity extends AppCompatActivity {
    private int currentQuestionIndex = 0;
    private TextView tvQuestion, tvQuestionNumber, text1, text2, text3, text4, tvScore;
    private Button btnNext;
    private CardView radioButton1, radioButton2, radioButton3, radioButton4;
    private List<String> questions;
    private ProgressBar progressBar;
    private int correctQuestion = 0;
    private int currentScore = 0;
    String txt;
    private ImageView cardBg, backLiterature, cardBg2, cardBg3, cardBg4;
    private Map<String, Map<String, Boolean>> questionsAnswerMap;
    private ArrayList<String> getQuestionsAnswerMap;
    private boolean answerSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_graph_and_liture);
        Intent intent = getIntent();
        String subject = intent.getStringExtra(Constants.SUBJECT);

        if (subject.equals(getString(R.string.literature))) {
            questionsAnswerMap = Utils.getRandomLiteratureAndGeographyQuestions(this, getString(R.string.literature), Constants.QUESTION_SHOWING);
        }
        initView();
        variantClick1();
        variantClick2();
        variantClick3();
        variantClick4();

        backLiterature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnNextQuestionLiteratureAndGeography).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {

                if (!txt.isEmpty() && !answerSelected) {
                    // Disable all options after selection
                    disableAllOptions();
                    answerSelected = true;

                    boolean answer = questionsAnswerMap.get(questions.get(currentQuestionIndex)).get(txt);
                    
                    // Show correct/incorrect colors
                    showAnswerFeedback(answer);
                    
                    // Update score
                    if (answer) {
                        correctQuestion++;
                        currentScore += 5; // +5 for correct answer
                    } else {
                        currentScore -= 2; // -2 for incorrect answer
                        if (currentScore < 0) currentScore = 0; // Don't go below 0
                    }
                    
                    // Update score display
                    updateScoreDisplay();
                    
                    // Change button text to "Next" and enable it
                    btnNext.setText("Next");
                    btnNext.setEnabled(true);
                    
                } else if (answerSelected && btnNext.getText().equals("Next")) {
                    // Move to next question
                    currentQuestionIndex++;
                    answerSelected = false;
                    variantClear();
                    enableAllOptions();
                    
                    if (currentQuestionIndex < Constants.QUESTION_SHOWING) {
                        displayNextQuestions();
                        btnNext.setText(getString(R.string.next));
                        btnNext.setEnabled(false);
                    } else {
                        Intent intentResult = new Intent(GeographyOrLiteratureQuizActivity.this, FinalResultActivity.class);
                        intentResult.putExtra(Constants.SUBJECT, subject);
                        intentResult.putExtra(Constants.CORRECT, correctQuestion);
                        intentResult.putExtra(Constants.TYPE, "literature");
                        intentResult.putExtra(Constants.INCORRECT, Constants.QUESTION_SHOWING - correctQuestion);
                        intentResult.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentResult);
                    }
                } else {
                    Toast.makeText(GeographyOrLiteratureQuizActivity.this, "Please select an option!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });


        displayData();


    }

    private void variantClick1() {
        radioButton1.setOnClickListener(v -> {
                if (answerSelected) return;
                    cardBg.setImageResource(R.drawable.set_checked_to_variant);
                    cardBg2.setImageResource(R.drawable.set_un_checked_to_variant);
                    cardBg3.setImageResource(R.drawable.set_un_checked_to_variant);
                    cardBg4.setImageResource(R.drawable.set_un_checked_to_variant);
                    txt = text1.getText().toString();
                }
        );
    }

    private void variantClick2() {
        radioButton2.setOnClickListener(v -> {
                if (answerSelected) return;
                    cardBg2.setImageResource(R.drawable.set_checked_to_variant);
                    cardBg.setImageResource(R.drawable.set_un_checked_to_variant);
                    cardBg3.setImageResource(R.drawable.set_un_checked_to_variant);
                    cardBg4.setImageResource(R.drawable.set_un_checked_to_variant);
                    txt = text2.getText().toString();

                }
        );
    }

    private void variantClick3() {
        radioButton3.setOnClickListener(v -> {
                if (answerSelected) return;
                    cardBg3.setImageResource(R.drawable.set_checked_to_variant);
                    cardBg2.setImageResource(R.drawable.set_un_checked_to_variant);
                    cardBg.setImageResource(R.drawable.set_un_checked_to_variant);
                    cardBg4.setImageResource(R.drawable.set_un_checked_to_variant);
                    txt = text3.getText().toString();

                }
        );
    }

    private void variantClick4() {
        radioButton4.setOnClickListener(v -> {
                if (answerSelected) return;
                    cardBg4.setImageResource(R.drawable.set_checked_to_variant);
                    cardBg2.setImageResource(R.drawable.set_un_checked_to_variant);
                    cardBg3.setImageResource(R.drawable.set_un_checked_to_variant);
                    cardBg.setImageResource(R.drawable.set_un_checked_to_variant);
                    txt = text4.getText().toString();

                }
        );
    }

    private void showAnswerFeedback(boolean isCorrect) {
        // Get correct answer
        String correctAnswer = "";
        for (Map.Entry<String, Boolean> entry : questionsAnswerMap.get(questions.get(currentQuestionIndex)).entrySet()) {
            if (entry.getValue()) {
                correctAnswer = entry.getKey();
                break;
            }
        }
        
        // Set colors based on answers
        setOptionBackground(text1.getText().toString(), correctAnswer, txt, radioButton1);
        setOptionBackground(text2.getText().toString(), correctAnswer, txt, radioButton2);
        setOptionBackground(text3.getText().toString(), correctAnswer, txt, radioButton3);
        setOptionBackground(text4.getText().toString(), correctAnswer, txt, radioButton4);
    }
    
    private void setOptionBackground(String optionText, String correctAnswer, String selectedAnswer, CardView cardView) {
        if (optionText.equals(correctAnswer)) {
            // Correct answer - green background
            cardView.setBackgroundResource(R.drawable.correct_answer_bg);
        } else if (optionText.equals(selectedAnswer)) {
            // Selected wrong answer - red background
            cardView.setBackgroundResource(R.drawable.incorrect_answer_bg);
        } else {
            // Other options - default background
            cardView.setBackgroundResource(R.drawable.default_answer_bg);
        }
    }
    
    private void disableAllOptions() {
        radioButton1.setEnabled(false);
        radioButton2.setEnabled(false);
        radioButton3.setEnabled(false);
        radioButton4.setEnabled(false);
    }
    
    private void enableAllOptions() {
        radioButton1.setEnabled(true);
        radioButton2.setEnabled(true);
        radioButton3.setEnabled(true);
        radioButton4.setEnabled(true);
    }
    
    private void updateScoreDisplay() {
        if (tvScore != null) {
            tvScore.setText("Score: " + currentScore);
        }
    }
    private void variantClear() {
        txt = "";
        cardBg.setImageResource(R.drawable.set_un_checked_to_variant);
        cardBg2.setImageResource(R.drawable.set_un_checked_to_variant);
        cardBg3.setImageResource(R.drawable.set_un_checked_to_variant);
        cardBg4.setImageResource(R.drawable.set_un_checked_to_variant);
    }

    @SuppressLint("SetTextI18n")
    private void displayNextQuestions() {
        setAnswersToRadioButton();
        tvQuestion.setText(questions.get(currentQuestionIndex));
        tvQuestionNumber.setText("" + (currentQuestionIndex + 1));
        progressBar.setProgress(currentQuestionIndex * 10);
        progressBar.setMax(100);


        if (currentQuestionIndex == Constants.QUESTION_SHOWING - 1) {
            btnNext.setText(getText(R.string.finish));
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayData() {
        tvQuestion.setText(questions.get(currentQuestionIndex));
        tvQuestionNumber.setText("" + (currentQuestionIndex + 1));

        setAnswersToRadioButton();
    }

    private void setAnswersToRadioButton() {

        ArrayList<String> questionKey = new ArrayList(questionsAnswerMap.get(questions.get(currentQuestionIndex)).keySet());

        text1.setText(questionKey.get(0));
        text2.setText(questionKey.get(1));
        text3.setText(questionKey.get(2));
        text4.setText(questionKey.get(3));

    }


    public void initView() {
        questions = new ArrayList<>(questionsAnswerMap.keySet());
        txt = "";

        tvQuestion = findViewById(R.id.textView78);
        tvQuestionNumber = findViewById(R.id.textView18);
        tvScore = findViewById(R.id.tvScore);
        btnNext = findViewById(R.id.btnNextQuestionLiteratureAndGeography);
        text1 = findViewById(R.id.radioButton1Text);
        text2 = findViewById(R.id.radioButto21Text);
        text3 = findViewById(R.id.radioButto3Text);
        text4 = findViewById(R.id.radioButton4Text);
        backLiterature = findViewById(R.id.backadabiy);
        cardBg = findViewById(R.id.setChecked);
        cardBg2 = findViewById(R.id.setChecked2);
        cardBg3 = findViewById(R.id.setChecked3);
        cardBg4 = findViewById(R.id.setChecked4);
        progressBar = findViewById(R.id.progressadabiy);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
        
        // Initialize score display
        updateScoreDisplay();
        btnNext.setEnabled(false);
    }
}