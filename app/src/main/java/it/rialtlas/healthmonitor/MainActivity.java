package it.rialtlas.healthmonitor;

import static it.rialtlas.healthmonitor.R.id;
import static it.rialtlas.healthmonitor.R.layout.activity_onco_support_main;
import static it.rialtlas.healthmonitor.R.layout.popup_layout;
import static it.rialtlas.healthmonitor.R.menu.onco_support_menu;
import static it.rialtlas.healthmonitor.R.string;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.worldgn.connector.Connector;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rialtlas.healthmonitor.Login.Constants;
import it.rialtlas.healthmonitor.Login.GetDataService;
import it.rialtlas.healthmonitor.controller.impl.HeloLXPlusControllerState;
import it.rialtlas.healthmonitor.model.MeasurementsContext;
import it.rialtlas.healthmonitor.model.MeasurementsContextStrategy;
import it.rialtlas.healthmonitor.utils.MessagingUtils;
import it.rialtlas.healthmonitor.view.Logout;
import it.rialtlas.healthmonitor.view.receivers.MeasurementReceiver;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Full-screen activity that shows and hides the system UI with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    static final private String TAG = MainActivity.class.getSimpleName();
    private static final int ALARM_REQUEST_CODE = 1 ;
    private static final long INTERVAL_MILLIS = 60 * 1000 ;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private MeasurementsContext measurementsContext;
    private MeasurementReceiver heloMeasurementReceiver;
    private StompConnection stompConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stompConnection = new StompConnection(); // Assicurati che "findViewById(android.R.id.content)" restituisca un riferimento alla vista di base corretta
        stompConnection.connect();

        // Ottieni un riferimento all'AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Recupera l'intento corrente
        Intent intent = getIntent();

// Recupera il token di accesso dall'extra dell'intento
        String accessToken = intent.getStringExtra("access_token");

// Utilizza il token di accesso come desiderato

        // Creazione di un Intent per il BroadcastReceiver
        Intent in = new Intent(this, NotificationReceiver.class);
        in.putExtra("user_id", "USER_ID"); // Sostituisci con l'ID delinquent corrente
        Log.d("provaaaa","provaaaa" + accessToken);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, 0);

        // Calcolo dell'orario di avvio dell'allarme
        long initialDelayMillis = System.currentTimeMillis(); // Avvia l'allarme immediatamente

        // Impostazione dell'allarme periodico per l'utente corrente
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, initialDelayMillis, INTERVAL_MILLIS, pendingIntent);

        // Crea il canale delle notifiche
        NotificationUtils.createNotificationChannel(this);

        // Invia una notifica
        NotificationUtils.sendNotification(this, "Benvenuto", "Hai effettuato l'accesso a OLOS");
        //
        // Initialize the MessagingUtils tools class
        //

        MessagingUtils.initialize(this);
        // Set permissions (Marshmallow+ Permission APIs)
        fuckMarshMallow();
        // Initialize the model
        this.measurementsContext = MeasurementsContextStrategy.getInstance(this.getApplication(), HeloLXPlusControllerState.STARTING);
        // this.measurementsContext.currentActivity(this);
        this.measurementsContext.currentActivity(this).state().process(measurementsContext);
        // Initialize te measurements receiver
        this.heloMeasurementReceiver = MeasurementReceiver.of(this);
        // Initialize the UI
        setContentView(activity_onco_support_main);
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        Calendar calendar = Calendar.getInstance();
        ((TextView) findViewById(id.currentDate)).setText(df.format(calendar.getTime()));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(onco_support_menu, menu);

        getMenuInflater().inflate(R.menu.bell, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //
        if(id == R.id.logoutButton) {

            Intent intentlogout= new Intent(this,Logout.class);
            startActivity(intentlogout);
            return true;
        }


        if (id == R.id.action_questionnaire) {
            Intent intent = new Intent(this, WebViewActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_feedback) {
            Intent emailIntent = new Intent(this, Segnalazione.class);
            startActivity(emailIntent);
            return true;
        }

        if (id == R.id.action_faq) {
            Intent intent = new Intent(this, FAQActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.devicesScan){
            measurementsContext.state().scan(measurementsContext);
            return true;}
        if(id== R.id.deviceUnbind){
            measurementsContext.state().unbind(measurementsContext);
            return true;}

        if (id == R.id.action_notification) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View popupView = inflater.inflate(popup_layout, null);

            // Creazione del popupWindow
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // Permette all'utente di interagire con il popup
            PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            // Impostazione della posizione del popup
            popupWindow.showAtLocation(findViewById(R.id.frameLayout), Gravity.CENTER, 0, 0);

            // Chiusura del popup quando viene fatto clic fuori dallo stesso
            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popupWindow.dismiss();
                    return true;
                }
            });

            return true;
        }

                return super.onOptionsItemSelected(item);
        }



    @Override
    protected void onDestroy() {
        try {
            Connector.getInstance().unbindDevice();
        } catch (Exception e) {
            // do nothing on this
            Log.w(TAG, "Unable to unbind device!");
        }
        stompConnection.disconnect();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                    Map<String, Integer> perms = new HashMap<>();
                    // Initial
                    perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                    // Fill with results
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for ACCESS_FINE_LOCATION
                    if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // All Permissions Granted
                        MessagingUtils.shortToast(string.MSG_ALL_PERMISSIONS_GRANTED);
                    } else {
                        // Permission Denied
                        MessagingUtils.shortToast(string.MSG_SOME_PERMISSION_DENIED);
                        finish();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void onResume() {
        super.onResume();
        registerReceiver(this.heloMeasurementReceiver, MeasurementReceiver.wristIntentFilter());
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.heloMeasurementReceiver);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void fuckMarshMallow() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Show Location");
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                StringBuilder sb = new StringBuilder();
                sb.append(MessagingUtils.translate(string.MSG_APP_NEEDS_ACCESS_TO));
                sb.append(" ");
                sb.append(permissionsNeeded.get(0));
                for (int i = 1; i < permissionsNeeded.size(); i++) {
                    sb.append(", ");
                    sb.append(permissionsNeeded.get(i));
                }
                MessagingUtils.oKCancelDialog(string.MSG_CONFIRM, sb.toString(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        },
                        null);
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        MessagingUtils.shortToast(string.MSG_NO_NEW_PERMISSION_REQUIRED);
    }

   @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            return shouldShowRequestPermissionRationale(permission);
        }
        return true;
    }
    //prova

    public void logout(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResponseBody> call= service.logout(Constants.CLIENT_ID,Constants.REFRESH_TOKEN,Constants.CLIENT_SECRET);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){

                    Intent op = new Intent(MainActivity.this,LoginActivity2.class);
                    startActivity(op);
                }
                else
                    Log.d("erroreeeee","erroreeeeeeee");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {



            }
        });
    }




}
