package com.example.musicplayerapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, NotificationReceiver.class);
        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    }
}
