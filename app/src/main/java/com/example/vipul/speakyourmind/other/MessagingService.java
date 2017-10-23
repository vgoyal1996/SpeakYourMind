package com.example.vipul.speakyourmind.other;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.vipul.speakyourmind.R;
import com.example.vipul.speakyourmind.activity.ChatActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingEvent";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.v(TAG,"From: "+remoteMessage.getFrom());
        Log.v(TAG,"Notification body: "+remoteMessage.getNotification().getBody());
        Log.v(TAG,"From2: "+remoteMessage.getData().get("receiver"));
        Log.v(TAG,remoteMessage.getData().toString());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!(prefs.getBoolean("isInForeground",true)&&prefs.getString("receiverID","-1").equals(remoteMessage.getData().get("receiver"))))
            sendNotification(remoteMessage.getData().get("sender"),remoteMessage.getNotification().getBody());
    }

    public void sendNotification(String from,String messageBody){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.PERSON_POS,from);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pd = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Speak Your Mind")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pd);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}
