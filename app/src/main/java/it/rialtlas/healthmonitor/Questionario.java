package it.rialtlas.healthmonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class Questionario extends AppCompatActivity {

    private TextView mQuestionTextView;
    private RadioGroup mAnswerRadioGroup;
    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionario);

        mQuestionTextView = findViewById(R.id.question_text_view);
        mAnswerRadioGroup = findViewById(R.id.answer_radio_group);
        mSubmitButton = findViewById(R.id.submit_button);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected radio button from the radio group
                int selectedId = mAnswerRadioGroup.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    // If no answer is selected, show a Toast message
                    Toast.makeText(Questionario.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                } else {
                    // Get the text of the selected radio button and show it in a Toast message
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String answerText = selectedRadioButton.getText().toString();
                    Toast.makeText(Questionario.this, "Your answer is: " + answerText, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
