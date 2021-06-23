package com.minorfish.clinicwaste.usb;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.felhr.usbserial.CDCSerialDevice;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.minorfish.clinicwaste.abs.App;
import com.tangjd.common.bluetooth.ByteUtil;
import com.tangjd.common.utils.Log;

/**
 * Author: Administrator
 * Date: 2018/3/21
 */

public class PrinterHelper {
    private static PrinterHelper sPrinterHelper;
    private Context mContext;

    public PrinterHelper(Context context) {
        mContext = context;
    }

    public static PrinterHelper getInstance() {
        if (sPrinterHelper == null) {
            sPrinterHelper = new PrinterHelper(App.getApp().getApplicationContext());
        }
        return sPrinterHelper;
    }

    public void connect(UsbDevice device) {
        UsbDeviceConnection connection = ((UsbManager) mContext.getSystemService(Context.USB_SERVICE)).openDevice(device);
        new ConnectionThread(device, connection, 115200, UsbSerialInterface.PARITY_NONE).start();
    }

    public void deviceDetached() {

    }

    private UsbSerialDevice mSerialPort;
    private boolean mConnected;

    public void printTest() {
        if (mConnected) {
            LabelBegin(384, 83);    //?????????????????
            LableText(10, 50, 2, 0, "1111111111");
            LableText(10, 100, 3, 0, "2222222222");
            LableText(10, 150, 12, 0, "3333333333");
            LableText(10, 150, 20, 0, "4444444444");
            Labelend();
//            mSerialPort.write(data);
//            Log.e("TTTTTT", "sent " + ByteUtil.bytesToHexString(data));
        } else {
            Log.e("TTTTTT", "print test fail: no connection" );
        }
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
                    serialPort.setParity(mParity); // 校验
                    /**
                     * Current flow control Options:
                     * UsbSerialInterface.FLOW_CONTROL_OFF
                     * UsbSerialInterface.FLOW_CONTROL_RTS_CTS only for CP2102 and FT232
                     * UsbSerialInterface.FLOW_CONTROL_DSR_DTR only for CP2102 and FT232
                     */
                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);

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



    public void LabelBegin(int var1, int var2) {
        int var10002 = var1;
        boolean var4 = false;
        int var3 = var2;
        var2 = var10002;
        var4 = false;
        var4 = false;
        byte[] var5;
        (var5 = new byte[12])[0] = 26;
        var5[1] = 91;
        var5[2] = 1;
        var5[3] = 0;
        var5[4] = 0;
        var5[5] = 0;
        var5[6] = 0;
        var5[7] = (byte)var2;
        var5[8] = (byte)(var2 >> 8);
        var5[9] = (byte)var3;
        var5[10] = (byte)(var3 >> 8);
        var5[11] = 0;
        mSerialPort.write(var5);
    }

    public void LableText(int var1, int var2, int var3, int var4, String var5) {
        mSerialPort.write(com.szzk.ttl_lablelibs.a.b(var1, var2, var3, var4, var5));
    }

    public void Labelend() {
        byte[] var10000 = new byte[]{29, 86, 65, 0};
        byte[] var1 = new byte[]{26, 93, 0, 26, 79, 0};
//        mSerialPort.write(com.szzk.ttl_lablelibs.a.c.a(new byte[][]{var1}));
        mSerialPort.write(var1);
    }
}
