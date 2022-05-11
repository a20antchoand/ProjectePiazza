package com.example.piazza.commons;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.example.piazza.controladores.auth.SplashScreen;
import com.example.testauth.R;

public class Notificacio {

    /**
     * Funcio per enviar notificacion al usuari
     *
     * @param context context que utilitza
     * @param titol titol de la notificacio
     * @param missatge missatge de la notificacio
     * @param notID id de la notificacio
     */

    public static void Notificar(Context context, String titol, String missatge, int notID){

            NotificationCompat.Builder creador;
            String canalID = "canalPiazza";

            NotificationManager notificador = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            new NotificationCompat.Builder(context, canalID);

            Intent notificationIntent = new Intent(context, context.getClass());
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            String canalNombre = "Mensajes";
            String canalDescribe = "Canal de mensajes";

            int importancia = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel miCanal = new NotificationChannel(canalID, canalNombre, importancia);

            miCanal.setDescription(canalDescribe);
            miCanal.enableLights(true);
            miCanal.setLightColor(Color.BLUE); // Esto no lo soportan todos los dispositivos
            miCanal.enableVibration(true);

            notificador.createNotificationChannel(miCanal);
            creador = new NotificationCompat.Builder(context, canalID);

            int iconoSmall = R.mipmap.ic_launcher;

            creador.setSmallIcon(iconoSmall);
            creador.setContentTitle(titol);
            creador.setContentText(missatge);
            creador.setContentIntent(intent);
            creador.setStyle(new NotificationCompat.BigTextStyle().bigText(missatge));
            creador.setChannelId(canalID);

            notificador.notify(notID, creador.build());

    }

}


