package com.lzf.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.locTest.R;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;


public class AutoUpdateService extends Service {
	
	private IntentFilter intentFilter; 
	private NetworkChangeReceiver networkChangeReceiver; 
	
	private String ak="5M5vNbgxfxgP0LfBjElQweGp"; //APIconsolekey
	//默认城市
	
	private String city;//城市
	private String date;//日期
	private String weather;//天气
	private String wind;//风向
	private String temperature;//温度
	private String  Time1;
	
	ArrayList<String>  alldate = new ArrayList<String> ();//全部日期数据
	ArrayList<String>  allweather = new ArrayList<String> ();//全部天气数据
	ArrayList<String>  alltemperature = new ArrayList<String> ();//全部温度数据
    public static int updatetime = 8 * 60 * 60 * 1000;  //自动更新时间
    public static String editcity;
    
    
    public void onCreate() { 
    	super.onCreate(); 
		intentFilter = new IntentFilter(); 
    	intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE"); 
    	networkChangeReceiver = new NetworkChangeReceiver(); 
    	registerReceiver(networkChangeReceiver, intentFilter);
    	editcity=Location.citydata;
    } 
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				UpdateWeather();  //更新天气
			}
		}).start();	
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = updatetime; 
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	
	/******************************************************** 
	*函数名称：UpdateWeather 
	*说明： 更新天气
	*********************************************************/ 
    public void UpdateWeather(){
    	if(!checknetwork()) {
    		 setnotify(0);           //  网络是否连接成功    不成功则发送通知0
    		 return;
    	}
    	if (editcity==null || editcity==""){
    		SharedPreferences sharedPreferences = getSharedPreferences("precity", Context.MODE_PRIVATE);
    		editcity=sharedPreferences.getString("name", "");
    	}
    
	    new Thread(runNow).start();              // 获取天气
		   try {
			   
			    Thread.sleep(500);
			   } catch (InterruptedException e) {
			    		// TODO Auto-generated catch block
			    e.printStackTrace();
			   }
		    if(!(alldate.get(0)==null)){
			     setnotify(1);             // 1代表成功获取城市及天气
		    }
		    else{
		    	setnotify(0); 
	    }
	
		
    	
    }
	
	
	/******************************************************** 
	*函数名称：checknetwork 
	*说明： 检测网络是否可用
	*********************************************************/ 
	protected boolean checknetwork(){
		ConnectivityManager connectionManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
	    NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo(); 
	    if (networkInfo != null && networkInfo.isAvailable()) {  
		    return true;
	    } 
	    else { 
	    	return false;
	    }
	}
	
	//发送通知
	@SuppressWarnings("deprecation")
	protected void setnotify(int net){

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.icon=R.drawable.weather;
		notification.when=System.currentTimeMillis();	
		notification.ledARGB = Color.GREEN;
		notification.ledOnMS = 1000;
		notification.ledOffMS = 1000;
        
		
		Intent intent = new Intent(this, mainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
					PendingIntent.FLAG_CANCEL_CURRENT);
	
		switch (net)
		{
		    case 1:    
		    	try{
		    	//成功获取天气
		    	notification.tickerText=alldate.get(0);
		    	notification.setLatestEventInfo(this,alldate.get(0),
		    city+"        "+allweather.get(0)+"        "+wind+"        "+temperature, pi);
		    	}
		    	catch(Exception e){
	    			notification.tickerText="获取天气异常";
		    		notification.setLatestEventInfo(this, "获取天气异常",
		    				"请重新连接网络", pi);
	    		}
		    	break;
		    case 2:                                            //没有获取地址
		    	 notification.tickerText="获取城市失败";
		    	 notification.setLatestEventInfo(this, "获取城市失败",
		 				"请重新连接网络", pi);  
		    	break;
		    case 0:
		    	if(weather==null){
		    		notification.tickerText="无法显示天气";
		    		notification.setLatestEventInfo(this, "无法显示天气状态",
		    				"网络无连接", pi);
		    	}
		    	else{
		    		try{
							notification.tickerText=alldate.get(0);
							notification.setLatestEventInfo(this, city+"        "+allweather.get(0)+"        "+wind+"        "+temperature,
									"网络无连接"+"       "+Time1, pi);
		    		}
		    		catch(Exception e){
		    			notification.tickerText="请重新连接网络";
			    		notification.setLatestEventInfo(this, "无法显示天气状态",
			    				"请重新连接网络", pi);
		    		}
		    	}
		    	default:
		}
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		manager.notify(1, notification);
		Thread.currentThread();            //防止通知发送太频繁出错
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	

	
	/********************************************************  
	*说明： 获取天气
	*********************************************************/ 
    	Runnable runNow =new Runnable(){
    		public void run() {
    			
    			URL url;
    			HttpURLConnection connection;
    			Reader read;
    			BufferedReader bufferReader;
    			try {
    				//城市转码
    				String urlcity = URLEncoder.encode(editcity, "UTF-8"); 
    				
    				//请求指令
    				String path="http://api.map.baidu.com/telematics/v2/weather?location="+urlcity+"&output=xml&ak="+ak+"";
    				url=new URL(path);
    				
    				//创建和服务端的连接
    				connection =(HttpURLConnection) url.openConnection();
    				
    				//获取读取的方式
    				read=new InputStreamReader(connection.getInputStream());
    				bufferReader=new BufferedReader(read);
    				
    				//获取服务器返回的字符串
    				String str;//读取每一行数据
    				StringBuffer buffer=new StringBuffer();//接受全部数据
    				while((str=bufferReader.readLine())!=null){
    					buffer.append(str + "\n");
    				}
    				
    				//关闭连接
    				read.close();  
    				connection.disconnect();
    				
    				//测试	
    				Log.d("转码是否成功",urlcity.toString());
    				Log.d("发出去的请求",path.toString());
    				Log.d("读取来的数据",buffer.toString());

    				//解析
    				xml(buffer.toString());//调用
    				Log.d("全部日期数据", alldate.toString());
    				Log.d("全部天气数据", allweather.toString());
    				Log.d("全部温度数据", alltemperature.toString());
    				
    				//异常处理	
    				} catch (MalformedURLException e) {				
    				   e.printStackTrace();
    				} catch (IOException e) {
    				   e.printStackTrace();
    				}
    		}
    		
    		//解析
    		public void xml(String buffer){
    			String code=buffer.toString();
    			String wordkey="<status>success</status>";
    			Pattern p=Pattern.compile(wordkey);
    			Matcher m=p.matcher(code);
    			if(m.find()){
    				
    				wordkey="<currentCity>(.*)</currentCity>";//匹配城市
    				p=Pattern.compile(wordkey);
    				m=p.matcher(code);
    				m.find();
    				city=m.group(1);//获取城市
    				//handler.sendEmptyMessage(0);//返回数据
    				Log.d("获取到的城市",city.toString());					
    				
    				wordkey="<date>(.*)</date>";//匹配日期
    				p=Pattern.compile(wordkey);
    				m=p.matcher(code);
    				boolean result1 = m.find();
    				while(result1) { 
    				date=m.group(1);//获取日期
    				alldate.add(date);//接受全部数据
    				//handler.sendEmptyMessage(0);//返回数据
    				Log.d("获取到的日期",date.toString());result1 = m.find();}
    				
    				wordkey="<weather>(.*)</weather>";//匹配天气
    				p=Pattern.compile(wordkey);
    				m=p.matcher(code);
    				boolean result2 = m.find();
    				while(result2) { 
    				weather=m.group(1);//获取天气
    				allweather.add(weather);//接受全部数据
    				//handler.sendEmptyMessage(0);//返回数据
    				Log.d("获取到的天气",weather.toString());result2 = m.find();}
    				
    				wordkey="<wind>(.*)</wind>";//匹配风向
    				p=Pattern.compile(wordkey);
    				m=p.matcher(code);
    				m.find();
    				//boolean result3 = m.find();
    				//while(result3) { 
    				wind=m.group(1);//获取风向
    				//handler.sendEmptyMessage(0);//返回数据
    				Log.d("获取到的风向",wind.toString());//result3 = m.find();}
    				
    				wordkey="<temperature>(.*)</temperature>";//匹配温度
    				p=Pattern.compile(wordkey);
    				m=p.matcher(code);
    				boolean result4 = m.find();
    				while(result4) { 
    				temperature=m.group(1);//获取温度
    				alltemperature.add(temperature);//接受全部数据
    				
    				//获取解析时间
    				Time time = new Time("GMT+8");  
    				time.setToNow();  
    				String Minute;
    				if (time.minute<10)
    				{
    					Minute="0"+Integer.toString(time.minute);
    				}
    				else
    				{
    					Minute=Integer.toString(time.minute);
    				}
    				Time1 = Integer.toString(time.month+1)+"月"+Integer.toString(time.monthDay)+"日"+Integer.toString(time.hour+8)+":"+Minute+"更新";
    				
    				result4 = m.find();

    				}	
    			}
    		}	
    	};
   	
    
	
    	/******************************************************** 
    	*说明： 网络变化则更新天气
    	*********************************************************/ 
	class NetworkChangeReceiver extends BroadcastReceiver { 
		 
		public void onReceive(Context context, Intent intent) {
			UpdateWeather();
		} 			 
	}    
}


