package com.lzf.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//���չ㲥������
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		 Intent ootStartIntent=new Intent(context,mainActivity.class); 
         ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
         context.startActivity(ootStartIntent); 
	}

}
