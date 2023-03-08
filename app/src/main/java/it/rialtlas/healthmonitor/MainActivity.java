package it.rialtlas.healthmonitor;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.worldgn.connector.Connector;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rialtlas.healthmonitor.controller.impl.HeloLXPlusControllerState;
import it.rialtlas.healthmonitor.model.MeasurementsContext;
import it.rialtlas.healthmonitor.model.MeasurementsContextStrategy;
import it.rialtlas.healthmonitor.utils.MessagingUtils;
import it.rialtlas.healthmonitor.view.receivers.MeasurementReceiver;

/**
 * Full-screen activity that shows and hides the system UI with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    static final private String TAG = MainActivity.class.getSimpleName();
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private MeasurementsContext measurementsContext;
    private MeasurementReceiver heloMeasurementReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the MessagingUtils tools class
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
        setContentView(R.layout.activity_onco_support_main);
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        Calendar calendar = Calendar.getInstance();
        ((TextView) findViewById(R.id.currentDate)).setText(df.format(calendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.onco_support_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_questionnaire) {
            Intent intent = new Intent(this, Questionario.class);
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
                        MessagingUtils.shortToast(R.string.MSG_ALL_PERMISSIONS_GRANTED);
                    } else {
                        // Permission Denied
                        MessagingUtils.shortToast(R.string.MSG_SOME_PERMISSION_DENIED);
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
                sb.append(MessagingUtils.translate(R.string.MSG_APP_NEEDS_ACCESS_TO));
                sb.append(" ");
                sb.append(permissionsNeeded.get(0));
                for (int i = 1; i < permissionsNeeded.size(); i++) {
                    sb.append(", ");
                    sb.append(permissionsNeeded.get(i));
                }
                MessagingUtils.oKCancelDialog(R.string.MSG_CONFIRM, sb.toString(),
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
        MessagingUtils.shortToast(R.string.MSG_NO_NEW_PERMISSION_REQUIRED);
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

}
