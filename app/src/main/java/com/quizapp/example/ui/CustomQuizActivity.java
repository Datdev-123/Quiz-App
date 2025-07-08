package com.quizapp.example.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quizapp.example.R;
import com.quizapp.example.local_data.QuizPref;
import com.quizapp.example.model.CustomQuestionModel;
import com.quizapp.example.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomQuizActivity extends AppCompatActivity {
    
    private int currentQuestionIndex = 0;
    private TextView tvQuestion, tvQuestionNumber, text1, text2, text3, text4, tvScore;
    private Button btnNext;
    private CardView radioButton1, radioButton2, radioButton3, radioButton4;
    private List<CustomQuestionModel> questions;
    private ProgressBar progressBar;
    private int correctQuestion = 0;
    private int currentScore = 0;
    private String selectedAnswer = "";
    private ImageView cardBg, backButton, cardBg2, cardBg3, cardBg4;
    private QuizPref quizPref;
    private boolean answerSelected = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_quiz);
        
        initViews();
        loadQuestions();
        setupClickListeners();
        displayCurrentQuestion();
    }
    
    private void initViews() {
        quizPref = QuizPref.getInstance();
        
        tvQuestion = findViewById(R.id.tvCustomQuestion);
        tvQuestionNumber = findViewById(R.id.tvCustomQuestionNumber);
        tvScore = findViewById(R.id.tvScoreCustom);
        btnNext = findViewById(R.id.btnNextCustomQuestion);
        text1 = findViewById(R.id.tvCustomOption1);
        text2 = findViewById(R.id.tvCustomOption2);
        text3 = findViewById(R.id.tvCustomOption3);
        text4 = findViewById(R.id.tvCustomOption4);
        backButton = findViewById(R.id.ivBackCustomQuiz);
        cardBg = findViewById(R.id.ivCustomChecked1);
        cardBg2 = findViewById(R.id.ivCustomChecked2);
        cardBg3 = findViewById(R.id.ivCustomChecked3);
        cardBg4 = findViewById(R.id.ivCustomChecked4);
        progressBar = findViewById(R.id.progressCustomQuiz);
        radioButton1 = findViewById(R.id.cvCustomOption1);
        radioButton2 = findViewById(R.id.cvCustomOption2);
        radioButton3 = findViewById(R.id.cvCustomOption3);
        radioButton4 = findViewById(R.id.cvCustomOption4);
        
        // Initialize score display
        updateScoreDisplay();
    }
    
    private void loadQuestions() {
        String json = quizPref.getCustomQuestions();
        if (json.isEmpty()) {
            questions = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CustomQuestionModel>>(){}.getType();
            questions = gson.fromJson(json, type);
        }
        
        // Shuffle questions for random order
        Collections.shuffle(questions);
        
        // Limit to 10 questions max
        if (questions.size() > 10) {
            questions = questions.subList(0, 10);
        }
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        radioButton1.setOnClickListener(v -> selectOption(0));
        radioButton2.setOnClickListener(v -> selectOption(1));
        radioButton3.setOnClickListener(v -> selectOption(2));
        radioButton4.setOnClickListener(v -> selectOption(3));
        
        btnNext.setOnClickListener(v -> handleNextQuestion());
    }
    
    private void selectOption(int optionIndex) {
        if (answerSelected) return;
        
        clearSelection();
        
        switch (optionIndex) {
            case 0:
                cardBg.setImageResource(R.drawable.set_checked_to_variant);
                selectedAnswer = text1.getText().toString();
                break;
            case 1:
                cardBg2.setImageResource(R.drawable.set_checked_to_variant);
                selectedAnswer = text2.getText().toString();
                break;
            case 2:
                cardBg3.setImageResource(R.drawable.set_checked_to_variant);
                selectedAnswer = text3.getText().toString();
                break;
            case 3:
                cardBg4.setImageResource(R.drawable.set_checked_to_variant);
                selectedAnswer = text4.getText().toString();
                break;
        }
    }
    
    private void clearSelection() {
        selectedAnswer = "";
        cardBg.setImageResource(R.drawable.set_un_checked_to_variant);
        cardBg2.setImageResource(R.drawable.set_un_checked_to_variant);
        cardBg3.setImageResource(R.drawable.set_un_checked_to_variant);
        cardBg4.setImageResource(R.drawable.set_un_checked_to_variant);
    }
    
    private void handleNextQuestion() {
        if (selectedAnswer.isEmpty() && !answerSelected) {
            Toast.makeText(this, "Please select an answer!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!answerSelected) {
            // First click - show answer feedback
            answerSelected = true;
            
            // Check if answer is correct
            CustomQuestionModel currentQuestion = questions.get(currentQuestionIndex);
            String correctAnswer = currentQuestion.getCorrectAnswer();
            
            boolean isCorrect = selectedAnswer.equals(correctAnswer);
            if (isCorrect) {
                correctQuestion++;
                currentScore += 5;
            } else {
                currentScore -= 2;
                if (currentScore < 0) currentScore = 0;
            }
            
            // Show answer feedback
            showAnswerFeedback(isCorrect, correctAnswer);
            
            // Update score display
            updateScoreDisplay();
            
            // Disable options and change button text
            disableAllOptions();
            btnNext.setText("Next");
            
        } else {
            // Second click - move to next question
            currentQuestionIndex++;
            answerSelected = false;
            clearSelection();
            enableAllOptions();
            
            if (currentQuestionIndex < questions.size()) {
                displayCurrentQuestion();
                btnNext.setText("Submit");
            } else {
                showResults();
            }
        }
    }
    
    private void showAnswerFeedback(boolean isCorrect, String correctAnswer) {
        // Set colors for all options
        setOptionBackground(text1.getText().toString(), correctAnswer, selectedAnswer, radioButton1);
        setOptionBackground(text2.getText().toString(), correctAnswer, selectedAnswer, radioButton2);
        setOptionBackground(text3.getText().toString(), correctAnswer, selectedAnswer, radioButton3);
        setOptionBackground(text4.getText().toString(), correctAnswer, selectedAnswer, radioButton4);
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
    
    @SuppressLint("SetTextI18n")
    private void displayCurrentQuestion() {
        if (currentQuestionIndex >= questions.size()) return;
        
        CustomQuestionModel question = questions.get(currentQuestionIndex);
        
        tvQuestion.setText(question.getQuestion());
        tvQuestionNumber.setText("" + (currentQuestionIndex + 1));
        
        text1.setText(question.getOption1());
        text2.setText(question.getOption2());
        text3.setText(question.getOption3());
        text4.setText(question.getOption4());
        
        progressBar.setProgress((currentQuestionIndex + 1) * 10);
        progressBar.setMax(questions.size() * 10);
        
        if (currentQuestionIndex == questions.size() - 1) {
            btnNext.setText("Complete");
        }
    }
    
    private void showResults() {
        Intent intent = new Intent(this, com.quizapp.example.ui.FinalResultActivity.class);
        intent.putExtra(Constants.SUBJECT, "Custom Quiz");
        intent.putExtra(Constants.CORRECT, correctQuestion);
        intent.putExtra(Constants.TYPE, "custom");
        intent.putExtra(Constants.INCORRECT, questions.size() - correctQuestion);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}