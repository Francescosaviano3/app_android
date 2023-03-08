package it.rialtlas.healthmonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;



public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        // Trova le TextView nel layout dell'attività e imposta il testo
        TextView question1TextView = findViewById(R.id.question1_textview);
        question1TextView.setText("Domanda 1");

        TextView answer1TextView = findViewById(R.id.answer1_textview);
        answer1TextView.setText("Risposta 1");

        TextView question2TextView = findViewById(R.id.question2_textview);
        question2TextView.setText("Domanda 2");

        TextView answer2TextView = findViewById(R.id.answer2_textview);
        answer2TextView.setText("Risposta 2");

        // Aggiungi altre domande e risposte qui

    }
}

