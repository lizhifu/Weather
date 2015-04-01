package com.lzf.weather;

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
		
	/******************************************************** 
	*�������ƣ�getlocation 
	*˵���� ��ȡ��ַ
	*********************************************************/ 
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
	
	/******************************************************** 
	*�������ƣ�setLocationOption 
	*˵���� ���û�ȡ��ַ����ز���
	*********************************************************/ 
	private void setLocationOption(){
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");		//������������
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setProdName("Handler_Position");
		option.setPoiExtraInfo(true);	// ��ϸ��ַ
		option.setAddrType("all");
		option.setScanSpan(50);// �����������ֵ���ڵ���1000��ms��ʱ����λSDK�ڲ�ʹ�ö�ʱ��λģʽ��
		option.setPriority(LocationClientOption.NetWorkFirst);      //������������
		//option.setPriority(LocationClientOption.GpsFirst);        //����gps����
		option.setPoiNumber(3);
		option.disableCache(true);		// �������û��涨λ����  
		mLocClient.setLocOption(option);
	} 
}