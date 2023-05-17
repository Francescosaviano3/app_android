package it.rialtlas.healthmonitor;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class StompConnection {
    private WebSocket webSocket;
    private Context context;
    private View parentView;

    public void connect() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("ws://your-websocket-url")
                .build();

        WebSocketListener webSocketListener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                // Connessione stabilita
                // Puoi inviare messaggi di sottoscrizione STOMP qui
                webSocket.send("SUBSCRIBE\n\ndestination:/topic/notification\n\n\0");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // Notifica ricevuta
                // Gestisci la notifica come desiderato

                // Mostra una notifica all'utente
                showNotification(text, (ViewGroup) parentView);
            }

            private void showNotification(String message, ViewGroup parentView) {
                // Creation del popup
                LayoutInflater inflater = LayoutInflater.from(context);
                View popupView = inflater.inflate(R.layout.popup_layout, null);

                // Creazione del popupWindow
                int width = ViewGroup.LayoutParams.WRAP_CONTENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // Permette all'utente di interagire con il popup
                PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // Impostazione della posizione del popup
                popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);

                // Chiusura del popup quando viene fatto clic fuori dallo stesso
                popupView.setOnTouchListener((v, event) -> {
                    popupWindow.dismiss();
                    return true;
                });
            }


            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                // Connessione chiusa
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // Errore di connessione
            }
        };

        webSocket = client.newWebSocket(request, webSocketListener);
    }

    public void disconnect() {
        webSocket.close(1000, null);
    }
}
