package it.rialtlas.healthmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Questionario extends AppCompatActivity {

    private TextView questionTextView;
    private RadioGroup answerRadioGroup;
    private RadioButton answerRadioButton1, answerRadioButton2, answerRadioButton3;
    private Button submitButton;

    private Question[] questions;
    private int currentQuestionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionario);

        // Initialize views
        questionTextView = findViewById(R.id.question_text_view);
        answerRadioGroup = findViewById(R.id.answer_radio_group);
        answerRadioButton1 = findViewById(R.id.answer1_radio_button);
        answerRadioButton2 = findViewById(R.id.answer2_radio_button);
        answerRadioButton3 = findViewById(R.id.answer4_radio_button);
        submitButton = findViewById(R.id.submit_button);

        // Create questions
        questions = new Question[]{
                new Question("What is the capital of Italy?", "Rome", "Paris", "Madrid"),
                new Question("What is the largest planet in our solar system?", "Jupiter", "Saturn", "Mars"),
                new Question("What is the most populous country in the world?", "China", "India", "USA")
        };

        // Set current question index to 0
        currentQuestionIndex = 0;

        // Set the first question
        setQuestion();

        // Add click listener to submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get selected answer
                int selectedAnswerId = answerRadioGroup.getCheckedRadioButtonId();
                if (selectedAnswerId == -1) {
                    // No answer selected
                    Toast.makeText(Questionario.this, "Please select an answer.", Toast.LENGTH_SHORT).show();
                } else {
                    // Answer selected
                    RadioButton selectedAnswerRadioButton = findViewById(selectedAnswerId);
                    String selectedAnswer = selectedAnswerRadioButton.getText().toString();

                    // Check if answer is correct
                    if (selectedAnswer.equals(questions[currentQuestionIndex].getAnswer())) {
                        Toast.makeText(Questionario.this, "Avanti!", Toast.LENGTH_SHORT).show();
                    }

                    // Move to next question
                    currentQuestionIndex++;
                    if (currentQuestionIndex == questions.length) {
                        // End of questions
                        Toast.makeText(Questionario.this, "End of questionario.", Toast.LENGTH_SHORT).show();
                        Intent b = new Intent(Questionario.this, MainActivity.class);
                        startActivity(b);
                    } else {
                        // Set the next question
                        setQuestion();
                    }
                }
            }
        });
    }

    private void setQuestion() {
        // Set question text and answer options
        questionTextView.setText(questions[currentQuestionIndex].getQuestion());
        answerRadioButton1.setText(questions[currentQuestionIndex].getOption1());
        answerRadioButton2.setText(questions[currentQuestionIndex].getOption2());
        answerRadioButton3.setText(questions[currentQuestionIndex].getOption3());

        // Clear selected answer
        answerRadioGroup.clearCheck();
    }
}

