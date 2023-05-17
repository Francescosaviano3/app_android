package it.rialtlas.healthmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Ottenere l'ID dell'utente a cui inviare la notifica
        String userId = intent.getStringExtra("user_id");

        // Mostrare la notifica per l'utente specifico
        showNotification(context, userId);
    }

    private void showNotification(Context context, String userId) {
        // Crea e mostra la notifica all'utente specifico
        // Utilizza l'ID dell'utente per personalizzare la notifica
    }
}
