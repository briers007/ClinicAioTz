package com.minorfish.clinicwaste.module.frame;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.App;
import com.minorfish.clinicwaste.module.ActPerson;
import com.minorfish.clinicwaste.module.abnormal.ActAbnormal;
import com.minorfish.clinicwaste.module.bagin.ActBagInList;
import com.minorfish.clinicwaste.module.boxout.ActBoxOut;
import com.minorfish.clinicwaste.module.query.ActQuery;
import com.minorfish.clinicwaste.module.signin.ActSignIn;
import com.minorfish.clinicwaste.update.UpdateHelper;
import com.minorfish.clinicwaste.usb.PrinterHelperSerial;
import com.minorfish.clinicwaste.usb.UsbService;
import com.tangjd.common.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author: tangjd
 * Date: 2017/6/12
 */

public class ActFrame extends BaseActivity {
    private static final String TAG = ActFrame.class.getSimpleName();
    @Bind(R.id.rv_frame)
    RvFrame rvFrame;

    @Bind(R.id.tvUser)
    TextView tvUser;

    private static final List<ResNameBean> sRvPanelData = new ArrayList<>();

    static {
        sRvPanelData.add(new ResNameBean("入库", R.mipmap.ic_bag_in));
        sRvPanelData.add(new ResNameBean("出库", R.mipmap.ic_box_out));
        sRvPanelData.add(new ResNameBean("出入库查询", R.mipmap.ic_query));
        sRvPanelData.add(new ResNameBean("异常记录", R.mipmap.ic_abnormal));
        // sRvPanelData.add(new ResNameBean("消息", R.mipmap.ic_msg));
        sRvPanelData.add(new ResNameBean("个人中心", R.mipmap.ic_person));
        sRvPanelData.add(new ResNameBean("退出", R.mipmap.ic_sign_out));
    }



    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_frame_layout);
        ButterKnife.bind(this);
//        setToolbarTitle(App.getApp().mUserBean.instName + "");
        rvFrame.setData(sRvPanelData);
        rvFrame.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0:
                        ActBagInList.startActivity(ActFrame.this);
                        //ScaleHelper.getInstance().getWeight();
                        break;
                    case 1:
                        ActBoxOut.startActivity(ActFrame.this);
//                        NfcHelper.getInstance().getTagId();
                        break;
                    case 2:
                        ActQuery.startActivity(ActFrame.this);
                        break;
                    case 3:
                        ActAbnormal.startActivity(ActFrame.this);
                        break;
                    case 4:
                        ActPerson.startActivity(ActFrame.this);
                        break;
                    case 5:
                        App.getApp().signOut();
//                        ActSignInPwd.startActivity(ActFrame.this);
                        //finish();
                        ActSignIn.startActivity(ActFrame.this);
                        //PrinterHelper.getInstance().printTest();
                        break;
                    default:
                        break;
                }
            }
        });
        onUsbCreate();

        PrinterHelperSerial.getInstance(this).connect();

        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(ActFrame.this,ActCalibration.class);
                startActivity(mIntent);
            }
        });
    }

    private void checkUpdate() {
        UpdateHelper.checkUpdate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onUsbDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUpdate();
        if (App.getApp().getSignInBean() != null && !TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
            tvUser.setText(App.getApp().getSignInBean().name+"");
        } else {
            ActSignIn.startActivity(ActFrame.this);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (App.getApp().getSignInBean() != null && !TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
        } else {
            ActSignIn.startActivity(ActFrame.this);
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ActFrame.class));
    }

    private void onUsbCreate() {
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    private void onUsbDestroy() {
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    /*
     * Notifications from UsbService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Log.w(TAG, "USB Ready " + device.toString());
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Log.w(TAG, "USB Permission not granted" + device.toString());
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Log.w(TAG, "USB disconnected" + device.toString());
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Log.w(TAG, "USB device not supported" + device.toString());
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Log.w(TAG, "No USB connected");
                    break;
            }
        }
    };
    private UsbService usbService;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };


    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }
}
