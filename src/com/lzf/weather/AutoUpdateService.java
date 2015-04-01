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
	//Ĭ�ϳ���
	
	private String city;//����
	private String date;//����
	private String weather;//����
	private String wind;//����
	private String temperature;//�¶�
	private String  Time1;
	
	ArrayList<String>  alldate = new ArrayList<String> ();//ȫ����������
	ArrayList<String>  allweather = new ArrayList<String> ();//ȫ����������
	ArrayList<String>  alltemperature = new ArrayList<String> ();//ȫ���¶�����
    public static int updatetime = 8 * 60 * 60 * 1000;  //�Զ�����ʱ��
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
				UpdateWeather();  //��������
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
	*�������ƣ�UpdateWeather 
	*˵���� ��������
	*********************************************************/ 
    public void UpdateWeather(){
    	if(!checknetwork()) {
    		 setnotify(0);           //  �����Ƿ����ӳɹ�    ���ɹ�����֪ͨ0
    		 return;
    	}
    	if (editcity==null || editcity==""){
    		SharedPreferences sharedPreferences = getSharedPreferences("precity", Context.MODE_PRIVATE);
    		editcity=sharedPreferences.getString("name", "");
    	}
    
	    new Thread(runNow).start();              // ��ȡ����
		   try {
			   
			    Thread.sleep(500);
			   } catch (InterruptedException e) {
			    		// TODO Auto-generated catch block
			    e.printStackTrace();
			   }
		    if(!(alldate.get(0)==null)){
			     setnotify(1);             // 1����ɹ���ȡ���м�����
		    }
		    else{
		    	setnotify(0); 
	    }
	
		
    	
    }
	
	
	/******************************************************** 
	*�������ƣ�checknetwork 
	*˵���� ��������Ƿ����
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
	
	//����֪ͨ
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
		    	//�ɹ���ȡ����
		    	notification.tickerText=alldate.get(0);
		    	notification.setLatestEventInfo(this,alldate.get(0),
		    city+"        "+allweather.get(0)+"        "+wind+"        "+temperature, pi);
		    	}
		    	catch(Exception e){
	    			notification.tickerText="��ȡ�����쳣";
		    		notification.setLatestEventInfo(this, "��ȡ�����쳣",
		    				"��������������", pi);
	    		}
		    	break;
		    case 2:                                            //û�л�ȡ��ַ
		    	 notification.tickerText="��ȡ����ʧ��";
		    	 notification.setLatestEventInfo(this, "��ȡ����ʧ��",
		 				"��������������", pi);  
		    	break;
		    case 0:
		    	if(weather==null){
		    		notification.tickerText="�޷���ʾ����";
		    		notification.setLatestEventInfo(this, "�޷���ʾ����״̬",
		    				"����������", pi);
		    	}
		    	else{
		    		try{
							notification.tickerText=alldate.get(0);
							notification.setLatestEventInfo(this, city+"        "+allweather.get(0)+"        "+wind+"        "+temperature,
									"����������"+"       "+Time1, pi);
		    		}
		    		catch(Exception e){
		    			notification.tickerText="��������������";
			    		notification.setLatestEventInfo(this, "�޷���ʾ����״̬",
			    				"��������������", pi);
		    		}
		    	}
		    	default:
		}
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		manager.notify(1, notification);
		Thread.currentThread();            //��ֹ֪ͨ����̫Ƶ������
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	

	
	/********************************************************  
	*˵���� ��ȡ����
	*********************************************************/ 
    	Runnable runNow =new Runnable(){
    		public void run() {
    			
    			URL url;
    			HttpURLConnection connection;
    			Reader read;
    			BufferedReader bufferReader;
    			try {
    				//����ת��
    				String urlcity = URLEncoder.encode(editcity, "UTF-8"); 
    				
    				//����ָ��
    				String path="http://api.map.baidu.com/telematics/v2/weather?location="+urlcity+"&output=xml&ak="+ak+"";
    				url=new URL(path);
    				
    				//�����ͷ���˵�����
    				connection =(HttpURLConnection) url.openConnection();
    				
    				//��ȡ��ȡ�ķ�ʽ
    				read=new InputStreamReader(connection.getInputStream());
    				bufferReader=new BufferedReader(read);
    				
    				//��ȡ���������ص��ַ���
    				String str;//��ȡÿһ������
    				StringBuffer buffer=new StringBuffer();//����ȫ������
    				while((str=bufferReader.readLine())!=null){
    					buffer.append(str + "\n");
    				}
    				
    				//�ر�����
    				read.close();  
    				connection.disconnect();
    				
    				//����	
    				Log.d("ת���Ƿ�ɹ�",urlcity.toString());
    				Log.d("����ȥ������",path.toString());
    				Log.d("��ȡ��������",buffer.toString());

    				//����
    				xml(buffer.toString());//����
    				Log.d("ȫ����������", alldate.toString());
    				Log.d("ȫ����������", allweather.toString());
    				Log.d("ȫ���¶�����", alltemperature.toString());
    				
    				//�쳣����	
    				} catch (MalformedURLException e) {				
    				   e.printStackTrace();
    				} catch (IOException e) {
    				   e.printStackTrace();
    				}
    		}
    		
    		//����
    		public void xml(String buffer){
    			String code=buffer.toString();
    			String wordkey="<status>success</status>";
    			Pattern p=Pattern.compile(wordkey);
    			Matcher m=p.matcher(code);
    			if(m.find()){
    				
    				wordkey="<currentCity>(.*)</currentCity>";//ƥ�����
    				p=Pattern.compile(wordkey);
    				m=p.matcher(code);
    				m.find();
    				city=m.group(1);//��ȡ����
    				//handler.sendEmptyMessage(0);//��������
    				Log.d("��ȡ���ĳ���",city.toString());					
    				
    				wordkey="<date>(.*)</date>";//ƥ������
    				p=Pattern.compile(wordkey);
    				m=p.matcher(code);
    				boolean result1 = m.find();
    				while(result1) { 
    				date=m.group(1);//��ȡ����
    				alldate.add(date);//����ȫ������
    				//handler.sendEmptyMessage(0);//��������
    				Log.d("��ȡ��������",date.toString());result1 = m.find();}
    				
    				wordkey="<weather>(.*)</weather>";//ƥ������
    				p=Pattern.compile(wordkey);
    				m=p.matcher(code);
    				boolean result2 = m.find();
    				while(result2) { 
    				weather=m.group(1);//��ȡ����
    				allweather.add(weather);//����ȫ������
    				//handler.sendEmptyMessage(0);//��������
    				Log.d("��ȡ��������",weather.toString());result2 = m.find();}
    				
    				wordkey="<wind>(.*)</wind>";//ƥ�����
    				p=Pattern.compile(wordkey);
    				m=p.matcher(code);
    				m.find();
    				//boolean result3 = m.find();
    				//while(result3) { 
    				wind=m.group(1);//��ȡ����
    				//handler.sendEmptyMessage(0);//��������
    				Log.d("��ȡ���ķ���",wind.toString());//result3 = m.find();}
    				
    				wordkey="<temperature>(.*)</temperature>";//ƥ���¶�
    				p=Pattern.compile(wordkey);
    				m=p.matcher(code);
    				boolean result4 = m.find();
    				while(result4) { 
    				temperature=m.group(1);//��ȡ�¶�
    				alltemperature.add(temperature);//����ȫ������
    				
    				//��ȡ����ʱ��
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
    				Time1 = Integer.toString(time.month+1)+"��"+Integer.toString(time.monthDay)+"��"+Integer.toString(time.hour+8)+":"+Minute+"����";
    				
    				result4 = m.find();

    				}	
    			}
    		}	
    	};
   	
    
	
    	/******************************************************** 
    	*˵���� ����仯���������
    	*********************************************************/ 
	class NetworkChangeReceiver extends BroadcastReceiver { 
		 
		public void onReceive(Context context, Intent intent) {
			UpdateWeather();
		} 			 
	}    
}


