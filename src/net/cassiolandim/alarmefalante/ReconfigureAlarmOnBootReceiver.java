package net.cassiolandim.alarmefalante;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReconfigureAlarmOnBootReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AlarmSetterService.class);
        MyApplication app = (MyApplication) context.getApplicationContext();
        service.setAction(app.getEnabled() ? AlarmSetterService.CREATE : AlarmSetterService.CANCEL);
        context.startService(service);
    }
}