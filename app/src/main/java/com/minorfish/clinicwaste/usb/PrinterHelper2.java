package com.minorfish.clinicwaste.usb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.minorfish.clinicwaste.abs.App;
import com.szzk.ttl_lablelibs.TTL_Factory;
import com.tangjd.common.utils.Log;

/**
 * Author: Administrator
 * Date: 2018/4/11
 */

public class PrinterHelper2 {
    private static PrinterHelper2 sPrinterHelper;
    private Context mContext;

    private TTL_Factory ttlf;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TTL_Factory.CONNECTION_STATE:
                    int k = msg.arg1;
                    if (k == 1) {
                        Log.e("TTTTTT", "打印机已连接");
                        Intent intent = new Intent(UsbService.ACTION_USB_READY);
                        mContext.sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(UsbService.ACTION_CDC_DRIVER_NOT_WORKING);
                        mContext.sendBroadcast(intent);
                    }
                    break;

                case TTL_Factory.CHECKPAGE_RESULT:
                    int kk = msg.arg1;
                    if (kk != 1) {
                        Toast.makeText(mContext, "打印机缺纸", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    public PrinterHelper2(Context context) {
        mContext = context;
    }

    public void printTest() {
        ttlf.LabelBegin(384, 250);    //?????????????????
        ttlf.LableText(10, 50, 2, 0, "fdsfdsfsdfsd");
        ttlf.LableText(10, 100, 3, 0, "fdsfdsfsdfsd");
        ttlf.LableText(10, 150, 12, 0, "fdsfdsfsdfsd");
        ttlf.LableText(10, 150, 20, 0, "fdsfdsfsdfsd");
        ttlf.Labelend();
    }

    public static PrinterHelper2 getInstance() {
        if (sPrinterHelper == null) {
            sPrinterHelper = new PrinterHelper2(App.getApp().getApplicationContext());
        }
        return sPrinterHelper;
    }

    public void connect(UsbDevice device) {
        ttlf = TTL_Factory.geTtl_Factory(mHandler);
        Log.e("TTTTTT", "portName " + device.getDeviceName().substring(5));
        ttlf.OpenPort(device.getDeviceName().substring(5), 115200);
    }

}
