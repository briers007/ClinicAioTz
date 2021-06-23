package com.minorfish.clinicwaste.usb;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import com.felhr.usbserial.CDCSerialDevice;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.minorfish.clinicwaste.abs.App;
import com.tangjd.common.bluetooth.ByteUtil;
import com.tangjd.common.utils.Log;

import java.io.UnsupportedEncodingException;

/**
 * Author: Administrator
 * Date: 2018/3/21
 */

public class ScaleHelper {
    private static ScaleHelper sScaleHelper;
    private Context mContext;
    public static final int MESSAGE_FROM_SERIAL_PORT = 0;
    private Handler mHandler;

    public ScaleHelper(Context context) {
        mContext = context;
    }

    public static ScaleHelper getInstance() {
        if (sScaleHelper == null) {
            sScaleHelper = new ScaleHelper(App.getApp().getApplicationContext());
        }
        return sScaleHelper;
    }

//    private UsbSerialInterface.UsbReadCallback mUsbReadCallback;
//
//    public void setOnUsbReadCallback(UsbSerialInterface.UsbReadCallback callback) {
//        mUsbReadCallback = callback;
//        if (mSerialPort != null) {
//            mSerialPort.read(mUsbReadCallback);
//        }
//    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    private UsbSerialInterface.UsbReadCallback mUsbReadCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0) {
            try {
                //String data = new String(arg0, "UTF-8");
                if (mHandler != null)
                    mHandler.obtainMessage(MESSAGE_FROM_SERIAL_PORT, arg0).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void connect(UsbDevice device) {
        UsbDeviceConnection connection = ((UsbManager) mContext.getSystemService(Context.USB_SERVICE)).openDevice(device);
        new ConnectionThread(device, connection, 9600, UsbSerialInterface.PARITY_NONE).start();
    }

    /*
     * State changes in the CTS line will be received here
     */
    private UsbSerialInterface.UsbCTSCallback ctsCallback = new UsbSerialInterface.UsbCTSCallback() {
        @Override
        public void onCTSChanged(boolean state) {
//            if (mHandler != null)
//                mHandler.obtainMessage(CTS_CHANGE).sendToTarget();
        }
    };

    /*
     * State changes in the DSR line will be received here
     */
    private UsbSerialInterface.UsbDSRCallback dsrCallback = new UsbSerialInterface.UsbDSRCallback() {
        @Override
        public void onDSRChanged(boolean state) {
//            if (mHandler != null)
//                mHandler.obtainMessage(DSR_CHANGE).sendToTarget();
        }
    };

    public void deviceDetached() {

    }

    private UsbSerialDevice mSerialPort;
    private boolean mConnected;

    public void sendMsg(byte[] data) {
        if (mConnected) {
            mSerialPort.write(data);
            Log.e("ScaleHelper", "sent " + ByteUtil.bytesToHexString(data));
        } else {
            Log.e("ScaleHelper", "send fail " + ByteUtil.bytesToHexString(data));
        }
    }

    public void zeroSet() {
        // sendMsg(new byte[]{0x3B, 0x02, 0x55, 0x1F, 0x55, 0x0A});
        sendMsg(new byte[]{0x02, 0x52, 0x5A, 0x52, 0x7F, (byte)0xFD, 0x0D});
    }


    public void getWeight() {
        // sendMsg(new byte[]{0x3B, 0x02, 0x55, 0x1F, 0x55, 0x0A});
        // sendMsg(new byte[]{0x02, 0x52, 0x5A, 0x52, 0x7F, 0xFD, 0x0D});
        sendMsg(new byte[]{0x3B, 0x02, 0x45, 0x45, 0x45, 0x0A});
    }

    /**
     * A simple thread to open a serial port.
     * Although it should be a fast operation. moving usb operations away from UI thread is a good thing.
     */
    public class ConnectionThread extends Thread {
        private UsbDevice mDevice;
        private UsbDeviceConnection mConnection;
        private int mBaudRate, mParity;

        public ConnectionThread(UsbDevice device, UsbDeviceConnection connection, int baudRate, int parity) {
            mDevice = device;
            mConnection = connection;
            mBaudRate = baudRate;
            mParity = parity;
        }

        @Override
        public void run() {
            UsbSerialDevice serialPort = UsbSerialDevice.createUsbSerialDevice(mDevice, mConnection);
            if (serialPort != null) {
                if (serialPort.open()) {
                    mConnected = true;
                    mSerialPort = serialPort;

                    serialPort.setBaudRate(mBaudRate);
                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8); // 数据位8位
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1); // 停止位1位
                    serialPort.setParity(mParity); // 奇偶校验
                    /**
                     * Current flow control Options:
                     * UsbSerialInterface.FLOW_CONTROL_OFF
                     * UsbSerialInterface.FLOW_CONTROL_RTS_CTS only for CP2102 and FT232
                     * UsbSerialInterface.FLOW_CONTROL_DSR_DTR only for CP2102 and FT232
                     */
                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    serialPort.read(mUsbReadCallback);
                    serialPort.getCTS(ctsCallback); // 校验用，一般不接
                    serialPort.getDSR(dsrCallback); // 校验用，一般不接

                    //
                    // Some Arduinos would need some sleep because firmware wait some time to know whether a new sketch is going
                    // to be uploaded or not
                    //Thread.sleep(2000); // sleep some. YMMV with different chips.

                    // Everything went as expected. Send an intent to MainActivity
                    Intent intent = new Intent(UsbService.ACTION_USB_READY);
                    mContext.sendBroadcast(intent);
                } else {
                    // Serial port could not be opened, maybe an I/O error or if CDC driver was chosen, it does not really fit
                    // Send an Intent to Main Activity
                    if (serialPort instanceof CDCSerialDevice) {
                        Intent intent = new Intent(UsbService.ACTION_CDC_DRIVER_NOT_WORKING);
                        mContext.sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(UsbService.ACTION_USB_DEVICE_NOT_WORKING);
                        mContext.sendBroadcast(intent);
                    }
                }
            } else {
                // No driver for given device, even generic CDC driver could not be loaded
                Intent intent = new Intent(UsbService.ACTION_USB_NOT_SUPPORTED);
                intent.putExtra(UsbManager.EXTRA_DEVICE, mDevice);
                mContext.sendBroadcast(intent);
            }
        }
    }
}
