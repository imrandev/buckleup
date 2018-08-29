package com.codzunk.buckleup.service.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codzunk.buckleup.ui.MainActivity;

public class TrackerBroadcastReceiver extends BroadcastReceiver {
    public TrackerBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent start = new Intent(context, MainActivity.class);
            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(start);
        }
    }
}
