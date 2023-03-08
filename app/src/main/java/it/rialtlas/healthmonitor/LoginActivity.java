package it.rialtlas.healthmonitor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameEditText = findViewById(R.id.username_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mLoginButton = findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the username and password from the EditText views
                String username = mUsernameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                // Call the LoginTask AsyncTask to send the login request
                new LoginTask().execute(username, password);
            }
        });
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // Get the username and password from the parameters
            String username = params[0];
            String password = params[1];

            // Build the URL for the login request
            String url = "https://192.168.6.15:8443/hr/admin/user/login" +
                    "?username=" + username + "&password=" + password;

            // Build the request using the Request.Builder class
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            // Create a new OkHttpClient instance to send the request
            OkHttpClient client = new OkHttpClient();

            // Send the request and get the response from the server
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                // Handle the response from the server here
                Log.d("Login response", response);
                Intent login = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(login);
            } else {
                // Log an error message if the response is null
                Log.e("Login response", "Response is null");
            }
        }
    }
}
