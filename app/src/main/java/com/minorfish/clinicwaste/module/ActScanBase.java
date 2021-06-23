package com.minorfish.clinicwaste.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanDevice;
import android.os.Bundle;

import com.minorfish.clinicwaste.BaseActivity;

/**
 * Author: tangjd
 * Date: 2017/6/13
 */

public abstract class ActScanBase extends BaseActivity {
    private ScanDevice mScanDevice;
    private final static String ACTION_SCAN = "scan.rcv.message";

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barocode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            String scanResult = new String(barocode, 0, barocodelen);
            mScanDevice.stopScan();

            callbackResult(scanResult);
        }
    };

    public abstract void callbackResult(String scanResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initScanDevice();
    }

    private void initScanDevice() {
        mScanDevice = new ScanDevice();
        mScanDevice.setOutScanMode(0); // 接收广播, 1为直接输出到文本框
        mScanDevice.setScanLaserMode(8); // 关闭连续扫码,4为开启连续扫码
    }


    @Override
    protected void onResume() {
        super.onResume();
        mScanDevice.openScan(); // 开启扫描开关
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SCAN);
        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mScanDevice.closeScan(); // 关闭扫描开关
            mScanDevice.stopScan(); // 停止扫描
            unregisterReceiver(mScanReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScanDevice = null;
    }
}
