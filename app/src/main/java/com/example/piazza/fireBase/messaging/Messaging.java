package com.example.piazza.fireBase.messaging;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Messaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Looper.prepare();

        Handler mHandler = new Handler();

        new Thread(() -> mHandler.post(() -> Toast.makeText(getApplicationContext(), "Notificacio", Toast.LENGTH_SHORT).show())).start();

        Looper.loop();
    }
}
