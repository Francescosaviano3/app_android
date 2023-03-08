package it.rialtlas.healthmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Segnalazione extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mMessageEditText;
    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.segnalazione);

        mNameEditText = findViewById(R.id.name_edit_text);
        mEmailEditText = findViewById(R.id.email_edit_text);
        mMessageEditText = findViewById(R.id.message_edit_text);
        mSendButton = findViewById(R.id.send_button);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameEditText.getText().toString();
                String email = mEmailEditText.getText().toString();
                String message = mMessageEditText.getText().toString();

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"feedback@example.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from my app");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\nEmail: " + email + "\n\n" + message);

                startActivity(Intent.createChooser(emailIntent, "Send feedback"));
            }
        });
    }
}
