package it.rialtlas.healthmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import it.rialtlas.healthmonitor.Login.AccessToken;
import it.rialtlas.healthmonitor.Login.GetDataService;
import it.rialtlas.healthmonitor.Login.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity2 extends AppCompatActivity {
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
            public void onClick(View view) {
                getAccessToken();
            }
        });


    }


    public void getAccessToken(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String password= mPasswordEditText.getText().toString();
        String username = mUsernameEditText.getText().toString();

        Call<AccessToken> call =service.getAccessToken("OLOS","password","vk7IOdVqvoOHeoD482MWAOVOlKnAfvKK","openid",username,password);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                AccessToken accessToken2 = response.body();
                Log.d("LoginActivity", "Response: " + response.body());
                if (response.isSuccessful()) {
                    Constants.REFRESH_TOKEN = response.body().getRefreshToken();
                    AccessToken accessToken = response.body();
                    Log.d("LoginActivity", "Response: " + response.body());

                    Intent i = new Intent(LoginActivity2.this,MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity2.this, "Erroreeeeeee", Toast.LENGTH_LONG).show();
                }
            }




            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
            Toast.makeText(LoginActivity2.this, "Error" + t, Toast.LENGTH_LONG).show();
            }
        });
    }

}