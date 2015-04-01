package com.lzf.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//接收广播自启动
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		 Intent ootStartIntent=new Intent(context,mainActivity.class); 
         ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
         context.startActivity(ootStartIntent); 
	}

}
