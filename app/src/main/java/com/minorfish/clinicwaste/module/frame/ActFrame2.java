package com.minorfish.clinicwaste.module.frame;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 南京秦淮一体秤
 */

public class ActFrame2 extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar_left_menu)
    TextView toolbarLeftMenu;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ll_ruku)
    LinearLayout llRuku;
    @Bind(R.id.ll_chuku)
    LinearLayout llChuku;
    @Bind(R.id.ll_query)
    LinearLayout llQuery;
    @Bind(R.id.ll_abnormal)
    LinearLayout llAbnormal;
    @Bind(R.id.ll_person)
    LinearLayout llPerson;
    @Bind(R.id.ll_exit)
    LinearLayout llExit;
    @Bind(R.id.tvUser)
    TextView tvUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_frame2_layout);
        ButterKnife.bind(this);

        llRuku.setOnClickListener(this);
        llChuku.setOnClickListener(this);
        llQuery.setOnClickListener(this);
        llAbnormal.setOnClickListener(this);
        llPerson.setOnClickListener(this);
        llExit.setOnClickListener(this);
        tvUser.setOnClickListener(this);

        onUsbCreate();
//        PrinterHelperSerial.getInstance(this).connect();//连接打印机
    }


    private void onUsbCreate() {
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
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


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Log.w("ActFrame2", "USB Ready " + device.toString());
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Log.w("ActFrame2", "USB Permission not granted" + device.toString());
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Log.w("ActFrame2", "USB disconnected" + device.toString());
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Log.w("ActFrame2", "USB device not supported" + device.toString());
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Log.w("ActFrame2", "No USB connected");
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (App.getApp().getSignInBean() != null && !TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
        } else {
            ActSignIn.startActivity(ActFrame2.this);
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ActFrame2.class));
    }


    private void onUsbDestroy() {
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
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
            tvUser.setText(App.getApp().getSignInBean().name + "");
        } else {
            ActSignIn.startActivity(ActFrame2.this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_ruku:
                ActBagInList.startActivity(ActFrame2.this);
                break;
            case R.id.ll_chuku:
                ActBoxOut.startActivity(ActFrame2.this);
                break;
            case R.id.ll_query:
                ActQuery.startActivity(ActFrame2.this);
                break;
            case R.id.ll_abnormal:
                ActAbnormal.startActivity(ActFrame2.this);
                break;
            case R.id.ll_person:
                ActPerson.startActivity(ActFrame2.this);
                break;
            case R.id.ll_exit:
                showAlertDialog("是否确认退出？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        App.getApp().signOut();
                        ActSignIn.startActivity(ActFrame2.this);
                    }
                }, null);

                break;
            case R.id.tvUser:
                ActCalibration.startActivity(ActFrame2.this);
                break;
            default:
                break;
        }
    }
}
