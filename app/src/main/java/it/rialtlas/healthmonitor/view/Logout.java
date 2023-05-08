package it.rialtlas.healthmonitor.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import it.rialtlas.healthmonitor.Login.Constants;
import it.rialtlas.healthmonitor.Login.GetDataService;
import it.rialtlas.healthmonitor.LoginActivity2;
import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.RetrofitClientInstance;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Logout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        //Button logoutButton = findViewById(R.id.logoutButton);
        //logoutButton.setOnClickListener(this);


    Button logoutButton = findViewById(R.id.logoutButton);
    logoutButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            logout();
        }
    });
    }

    public void logout() {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResponseBody> call = service.logout(Constants.CLIENT_ID, Constants.REFRESH_TOKEN, Constants.CLIENT_SECRET);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Intent pot = new Intent(Logout.this, LoginActivity2.class);
                    startActivity(pot);

                } else {
                    try {
                        // gestione errore
                        String errorResponse = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // gestione errore
            }
        });
    }
}
