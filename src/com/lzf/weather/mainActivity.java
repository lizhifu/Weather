package com.lzf.weather;

import java.util.regex.Pattern;

import com.baidu.locTest.R;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class mainActivity extends Activity {
	private TextView mTv = null;
	private LocationClient mLocClient;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		mTv = (TextView)findViewById(R.id.textview);
		mLocClient = ((Location)getApplication()).mLocationClient;
		((Location)getApplication()).mTv = mTv;
        getlocation();	
	}
		
	public void getlocation(){
		mLocClient.start();
		setLocationOption();
		mLocClient.stop();
	}
	@Override
	public void onDestroy() {
		mLocClient.stop();
		((Location)getApplication()).mTv = null;
		super.onDestroy();
	}
	//设置相关参数
	private void setLocationOption(){
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");		//设置坐标类型
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setProdName("Handler_Position");
		option.setPoiExtraInfo(true);	// 详细地址
		option.setAddrType("all");
		option.setScanSpan(50);// 当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
		option.setPriority(LocationClientOption.NetWorkFirst);      //设置网络优先
		//option.setPriority(LocationClientOption.GpsFirst);        //设置gps优先
		option.setPoiNumber(3);
		option.disableCache(true);		// 禁用启用缓存定位数据  
		mLocClient.setLocOption(option);
	}
	protected boolean isNumeric(String str) {   
		Pattern pattern = Pattern.compile("[0-9]*");   
		return pattern.matcher(str).matches();   
	}  
}