package net.cassiolandim.alarmefalante;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReconfigureAlarmOnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, AlarmSetterService.class);
		service.setAction(AlarmSetterService.RECONFIGURE);
		context.startService(service);
	}
}