package com.minorfish.clinicwaste.module.signin;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.Api;
import com.minorfish.clinicwaste.abs.App;
import com.minorfish.clinicwaste.abs.Result;
import com.minorfish.clinicwaste.module.frame.ActFrame;
import com.minorfish.clinicwaste.usb.NfcHelper;
import com.minorfish.clinicwaste.usb.UsbService;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.ByteUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.Set;

/**
 * Created by tangjd on 2016/8/8.
 */
public class ActSignIn extends BaseActivity {

    private static final String TAG = ActSignIn.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.act_sign_in_layout);
        initView();
        //onUsbCreate();

        NfcHelper.getInstance().setOnUsbReadCallback(new UsbService.OnUsbReadCallback() {
            @Override
            public void onUsbDataReceived(byte[] data) {
                //Log.e("TTTTTT", ByteUtil.bytesToHexStringWithoutSpace(data));
                String tagId = ByteUtil.bytesToHexStringWithoutSpace(ByteUtil.subByteArr(data, 0, 4));
                onNfcResultGet(tagId);
            }

            @Override
            public void onError(String error) {
                //showTipDialog(error);
            }
        });
        getSanzeData();
    }

    private void onNfcResultGet(final String tagId) {
        Log.i(TAG, "onNfcResultGet: "+tagId);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgressDialog();
                Api.login(tagId, new JsonApiBase.OnJsonResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Result result = Result.parse(response);
                        String resJson = response.toString();
                        Log.i(TAG, "onResponse: "+resJson);
                        if (result.isSuccess()) {
                            final SignInBean bean = SignInBean.objectFromData(result.mData + "");
                            if (bean == null) {
                                onError("服务器请求异常，请稍后重试");
                            } else {
                                dismissProgressDialog();
                                showShortToast("登录成功");
                                App.getApp().setSignInBean(result.mData + "", bean);
                                App.getApp().mToken = bean.token;
                                //ActFrame.startActivity(ActSignIn.this);
                                finish();
                            }
                        } else {
                            Log.i(TAG, "onResponse: "+result.mMsg);
                            onError(result.mMsg);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        dismissProgressDialog();
                        Log.i(TAG, "onError: "+error);
                        showTipDialog(error + "");
                    }

                    @Override
                    public void onFinish(boolean withoutException) {
                    }
                });
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        if (App.getApp().getSignInBean() != null && !TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
//            ActFrame.startActivity(ActSignIn.this);
//            finish();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
//        if (App.getApp().getSignInBean() != null && !TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
//            ActFrame.startActivity(ActSignIn.this);
//            finish();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {
        setToolbarTitle("登录");
        findViewById(R.id.toolbar).setBackgroundColor(Color.TRANSPARENT);

        ImageView ivAnim1 = (ImageView) findViewById(R.id.iv_anim_1);
        ImageView ivAnim2 = (ImageView) findViewById(R.id.iv_anim_2);
        final ImageView ivAnim3 = (ImageView) findViewById(R.id.iv_anim_3);

        // rotate animation of ivAnim1
        RotateAnimation rotateAnimation = new RotateAnimation(0, 359, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        // 单次动画时间
        rotateAnimation.setDuration(6000);
        // 无限循环
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        // 设置不停顿
        rotateAnimation.setInterpolator(new LinearInterpolator());
        ivAnim1.setAnimation(rotateAnimation);
        rotateAnimation.startNow();

        // scale animation of ivAnim2 and ivAnim3
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 2.6f, 1f, 2.6f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(2000);
        scaleAnimation.setRepeatCount(ScaleAnimation.INFINITE);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        ivAnim2.setAnimation(scaleAnimation);
        scaleAnimation.startNow();


//        findViewById(R.id.iv_anim_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onNfcResultGet("4282FCA5");
//            }
//        });
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //onUsbDestroy();
        if(mHandler!=null) {
            mHandler.removeCallbacks(getDataRunnable);
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void getSanzeData() {
        mHandler.removeCallbacks(getDataRunnable);
        mHandler.postDelayed(getDataRunnable, 600);
    }

    private Handler mHandler = new Handler();
    private Runnable getDataRunnable = new Runnable() {
        @Override
        public void run() {
            NfcHelper.getInstance().getTagId();
            mHandler.postDelayed(this,  600);
        }
    };


//    private void onUsbCreate() {
//        setFilters();  // Start listening notifications from UsbService
//        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
//    }
//
//    private void onUsbDestroy() {
//        unregisterReceiver(mUsbReceiver);
//        unbindService(usbConnection);
//    }
//
//    private void setFilters() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
//        filter.addAction(UsbService.ACTION_NO_USB);
//        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
//        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
//        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
//        registerReceiver(mUsbReceiver, filter);
//    }
//
//    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//            switch (intent.getAction()) {
//                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
//                    Log.w("TTTTTT", "USB Ready " + device.toString());
//                    break;
//                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
//                    Log.w("TTTTTT", "USB Permission not granted" + device.toString());
//                    break;
//                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
//                    Log.w("TTTTTT", "USB disconnected" + device.toString());
//                    break;
//                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
//                    Log.w("TTTTTT", "USB device not supported" + device.toString());
//                    break;
//                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
//                    Log.w("TTTTTT", "No USB connected");
//                    break;
//            }
//        }
//    };
//    private UsbService usbService;
//    private final ServiceConnection usbConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
//            usbService = ((UsbService.UsbBinder) arg1).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            usbService = null;
//        }
//    };
//
//
//    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
//        if (!UsbService.SERVICE_CONNECTED) {
//            Intent startService = new Intent(this, service);
//            if (extras != null && !extras.isEmpty()) {
//                Set<String> keys = extras.keySet();
//                for (String key : keys) {
//                    String extra = extras.getString(key);
//                    startService.putExtra(key, extra);
//                }
//            }
//            startService(startService);
//        }
//        Intent bindingIntent = new Intent(this, service);
//        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ActSignIn.class));
    }
}
