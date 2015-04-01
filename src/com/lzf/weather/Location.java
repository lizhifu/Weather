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
	 * 显示字符串
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
	    String xian = "市"+"(.*)"+"县";              //县
	    Pattern p = Pattern.compile(xian);
	    Matcher m = p.matcher(src);
	    if(m.find()){
	        return m.group(1);
	    }
	    //2015/04/01  注释掉区，由于部分区的天气无法获取，例如广州越秀区
//	    else{                                               
//	    	 String qu = "市"+"(.*)"+"区";         //区           
//	    	 p = Pattern.compile(qu);
//	    	 m = p.matcher(src);
//	    	 if (m.find()){
//	    		 return m.group(1);
//	    	 }
	    	 else{
			    	 String shi = "省"+"(.*)"+"市";  //市
			    	 p = Pattern.compile(shi);
			    	 m = p.matcher(src);
			    	  if(m.find()){
					        return m.group(1);
					  }
			    	  else{
			    		  shi= "中国"+"(.*)"+"市";   //直辖市
			    		  p = Pattern.compile(shi);
					      m = p.matcher(src);
					      if(m.find()){
						        return m.group(1);
						  }
					      else{
			    		     xian = "省"+"(.*)"+"县";   //直辖县
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
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			
			
		if(!(location.getAddrStr()==null)){
				
				citydata=getcity("中国"+location.getAddrStr());
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
						     "亲，网络无连接，请打开网络后再试", Toast.LENGTH_LONG);
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
				
				citydata=getcity("中国"+poiLocation.getAddrStr());
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
						     "亲，网络无连接，请打开网络后再试", Toast.LENGTH_LONG);
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