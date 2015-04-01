package com.lzf.weather;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.location.*;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


public class Location extends Application {

	public LocationClient mLocationClient = null;
	private String mData;  
	public MyLocationListenner myListener = new MyLocationListenner();
	public TextView mTv;
	public static String citydata;

	@Override
	public void onCreate() {
		mLocationClient = new LocationClient( this );
		mLocationClient.registerLocationListener( myListener );
		super.onCreate(); 
	
	}
	
	/**
	 * ��ʾ�ַ���
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			mData = str;
			if ( mTv != null ){
			mTv.setText(mData);
			Intent intent = new Intent(this, AutoUpdateService.class);
			startService(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected String getcity(String src){
	    String xian = "��"+"(.*)"+"��";              //��
	    Pattern p = Pattern.compile(xian);
	    Matcher m = p.matcher(src);
	    if(m.find()){
	        return m.group(1);
	    }
	    //2015/04/01  ע�͵��������ڲ������������޷���ȡ���������Խ����
//	    else{                                               
//	    	 String qu = "��"+"(.*)"+"��";         //��           
//	    	 p = Pattern.compile(qu);
//	    	 m = p.matcher(src);
//	    	 if (m.find()){
//	    		 return m.group(1);
//	    	 }
	    	 else{
			    	 String shi = "ʡ"+"(.*)"+"��";  //��
			    	 p = Pattern.compile(shi);
			    	 m = p.matcher(src);
			    	  if(m.find()){
					        return m.group(1);
					  }
			    	  else{
			    		  shi= "�й�"+"(.*)"+"��";   //ֱϽ��
			    		  p = Pattern.compile(shi);
					      m = p.matcher(src);
					      if(m.find()){
						        return m.group(1);
						  }
					      else{
			    		     xian = "ʡ"+"(.*)"+"��";   //ֱϽ��
					    	 p = Pattern.compile(xian);
					    	 m = p.matcher(src);
					    	 if(m.find()){
							        return m.group(1);
							  }
					    	  else{
			    		         return null;
					    	  }
					      }
			    	  }
//	          }
	    }
   }
	/**
	 * ��������������λ�õ�ʱ�򣬸�ʽ�����ַ������������Ļ��
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			
			
		if(!(location.getAddrStr()==null)){
				
				citydata=getcity("�й�"+location.getAddrStr());
				SharedPreferences sharedPreferences = getSharedPreferences("precity", Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				editor.clear();
				editor.putString("name", citydata);
				editor.commit();
				logMsg(citydata);
			}
			else{
				SharedPreferences sharedPreferences = getSharedPreferences("precity", Context.MODE_PRIVATE);
				String name = sharedPreferences.getString("name", "");
				if( name==null ){
					Toast toast = Toast.makeText(getApplicationContext(),
						     "�ף����������ӣ�������������", Toast.LENGTH_LONG);
						   toast.setGravity(Gravity.CENTER, 0, 0);
						   toast.show();
						   try {
							    Thread.sleep(5000);
							   } catch (InterruptedException e) {
							    		// TODO Auto-generated catch block
							    e.printStackTrace();
							   }
						   System.exit(0);
				}
				else {
					citydata=name;
					logMsg(citydata);
				}
					   
			 }
		 
	
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ; 
			}
			if(!(poiLocation.getAddrStr()==null)){
				
				citydata=getcity("�й�"+poiLocation.getAddrStr());
				SharedPreferences sharedPreferences = getSharedPreferences("precity", Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				editor.putString("name", citydata);
				logMsg(citydata);
			}
			else{
				SharedPreferences sharedPreferences = getSharedPreferences("precity", Context.MODE_PRIVATE);
				String name = sharedPreferences.getString("name", "");
				if( name==null ){
					Toast toast = Toast.makeText(getApplicationContext(),
						     "�ף����������ӣ�������������", Toast.LENGTH_LONG);
						   toast.setGravity(Gravity.CENTER, 0, 0);
						   toast.show();
						   try {
							    Thread.sleep(5000);
							   } catch (InterruptedException e) {
							    		// TODO Auto-generated catch block
							    e.printStackTrace();
							   }
						   System.exit(0);
				}
				else {
					citydata=name;
					logMsg(citydata);
				}
					   
			 }
		 }
	}
	
}