//package com.minorfish.clinicwaste.aio.printer;
//
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.TextView;
//
//import com.szzk.ttl_lablelibs.TTL_Factory;
//
//public class MainActivity extends Activity {
//	public static TTL_Factory ttlf;
//	private Handler mHandler;
//	private TextView datareceived_tv;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_main);
//		datareceived_tv=(TextView)findViewById(R.id.datareceived_tv);
//		mHandler=new MHandler();
//		ttlf= TTL_Factory.geTtl_Factory(mHandler);
//		SharedPreferencesUtil.initPreferences(this);
//		set_prl=(PercentRelativeLayout)findViewById(R.id.set_prl);
//		set_prl.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				Intent intent=new Intent(MainActivity.this,SetActivity.class);
//				startActivity(intent);
//			}
//		});
//		testserialport_prl=(PercentRelativeLayout)findViewById(R.id.testserialport_prl);
//		testprint_prl=(PercentRelativeLayout)findViewById(R.id.testprint_prl);
//		testprint_prl.setOnClickListener(new OnClickListener() {
//			@SuppressWarnings("static-access")
//			@Override
//			public void onClick(View arg0) {
//
//				if(!ttlf.isConnection())
//				{
//					Toast_Util.ToastString(getApplicationContext(), "A serial port did not open �?");
//					return;
//				}
//				Intent intent=new Intent(MainActivity.this,LablePrintActivity.class);
//				startActivity(intent);
//
//			}
//		});
//
//		testserialport_prl.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				String baudrate=SharedPreferencesUtil.getbaud_rate();
//				String com_name=SharedPreferencesUtil.getcom_name();
//				if(!baudrate.equals("")&&!com_name.equals(""))
//				{
//					Log.e("==========","=========baudrate="+baudrate);
//					Log.e("==========","=========com_name="+com_name);
//					ttlf.OpenPort(com_name, Integer.parseInt(baudrate));
//				}else {
//					ttlf.OpenPort("ttySAC0", 9600);
//				}
//			}
//		});
//
//	}
//
//	String streee="";
//	@SuppressLint("HandlerLeak")
//	class MHandler extends Handler {
//		@Override
//		public void handleMessage(Message msg) {
//
//			switch (msg.what) {
//			/**
//			 * DrawerService �� onStartCommand�ᷢ��������?	���ӳɹ��󷵻صĲ���
//			 */
//			case TTL_Factory.CONNECTION_STATE: {
//				int k=msg.arg1;
//				if(k==1)
//				{
//					Toast_Util.ToastString(getApplicationContext(), "A serial port opened successfully");
//				}else {
//					Toast_Util.ToastString(getApplicationContext(), "A serial port open failure");
//				}
//				break;
//			}
//			case TTL_Factory.CHECKPAGE_RESULT: {
//				int kk=msg.arg1;
//				Log.e("===========","�?查缺纸返�?=="+kk);
//				if(kk==1)
//				{
//					Toast_Util.ToastString(getApplicationContext(), "The printer has a paper");
//				}else {
//					Toast_Util.ToastString(getApplicationContext(), "There was no paper in the printer");
//				}
//				break;
//			}
//
//			}
//		}
//	}
//}
