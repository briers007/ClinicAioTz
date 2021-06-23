package com.minorfish.clinicwaste.usb;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.tangjd.common.bluetooth.ByteUtil;

import java.util.HashMap;
import java.util.Map;

public class UsbService extends Service {

    public static final String ACTION_USB_READY = "com.felhr.connectivityservices.USB_READY";
    public static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public static final String ACTION_USB_NOT_SUPPORTED = "com.felhr.usbservice.USB_NOT_SUPPORTED";
    public static final String ACTION_NO_USB = "com.felhr.usbservice.NO_USB";
    public static final String ACTION_USB_PERMISSION_GRANTED = "com.felhr.usbservice.USB_PERMISSION_GRANTED";
    public static final String ACTION_USB_PERMISSION_NOT_GRANTED = "com.felhr.usbservice.USB_PERMISSION_NOT_GRANTED";
    public static final String ACTION_USB_DISCONNECTED = "com.felhr.usbservice.USB_DISCONNECTED";
    public static final String ACTION_CDC_DRIVER_NOT_WORKING = "com.felhr.connectivityservices.ACTION_CDC_DRIVER_NOT_WORKING";
    public static final String ACTION_USB_DEVICE_NOT_WORKING = "com.felhr.connectivityservices.ACTION_USB_DEVICE_NOT_WORKING";
    public static final int MESSAGE_FROM_SERIAL_PORT = 0;
    public static final int CTS_CHANGE = 1;
    public static final int DSR_CHANGE = 2;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static boolean SERVICE_CONNECTED = false;

    private IBinder binder = new UsbBinder();

    private UsbManager usbManager;

//    private UsbDevice device;
//    private UsbDeviceConnection connection;
//    private UsbSerialDevice serialPort;
//    private boolean serialPortConnected;

//    public Map<UsbDevice, UsbBean> mUsbDeviceMap = new HashMap<>();

    /*
     * Different notifications from OS will be received here (USB attached, detached, permission responses...)
     * About BroadcastReceiver: http://developer.android.com/reference/android/content/BroadcastReceiver.html
     */
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            UsbDevice device = arg1.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            Log.e("UsbService", arg1.getAction() + "  " + (device == null ? "null" : device.toString()));
            if (arg1.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = arg1.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) // User accepted our USB connection. Try to open the device as a serial port
                {
                    Intent intent = new Intent(ACTION_USB_PERMISSION_GRANTED);
                    intent.putExtra(UsbManager.EXTRA_DEVICE, device);
                    arg0.sendBroadcast(intent);
                    switch (device.getVendorId()) {
                        case 6790:
                            ScaleHelper.getInstance().connect(device);
                            break;
                        case 1839:
                            NfcHelper.getInstance().connect(device);
                            break;
                        case 1659:
                            PrinterHelper.getInstance().connect(device);
                        default:
                            break;
                    }
                } else // User not accepted our USB connection. Send an Intent to the Main Activity
                {
                    Intent intent = new Intent(ACTION_USB_PERMISSION_NOT_GRANTED);
                    intent.putExtra(UsbManager.EXTRA_DEVICE, device);
                    arg0.sendBroadcast(intent);
                }
            } else if (arg1.getAction().equals(ACTION_USB_ATTACHED)) {
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();
                if (deviceVID != 0x1d6b && (devicePID != 0x0001 && devicePID != 0x0002 && devicePID != 0x0003) && deviceVID != 0x5c6 && devicePID != 0x904c) {
                    // There is a device connected to our Android device. Try to open it as a Serial Port.
                    requestUserPermission(device);
//                    keep = false;
                }
            } else if (arg1.getAction().equals(ACTION_USB_DETACHED)) {
                // Usb device was disconnected. send an intent to the Main Activity
                Intent intent = new Intent(ACTION_USB_DISCONNECTED);
                intent.putExtra(UsbManager.EXTRA_DEVICE, device);
                arg0.sendBroadcast(intent);
                switch (device.getVendorId()) {
                    case 6790:
                        ScaleHelper.getInstance().deviceDetached();
                        break;
                    case 1839:
                        NfcHelper.getInstance().deviceDetached();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    /*
     * onCreate will be executed when service is started. It configures an IntentFilter to listen for
     * incoming Intents (USB ATTACHED, USB DETACHED...) and it tries to open a serial port.
     */
    @Override
    public void onCreate() {
        UsbService.SERVICE_CONNECTED = true;
        setFilter();
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        findSerialPortDevice();
    }

    /* MUST READ about services
     * http://developer.android.com/guide/components/services.html
     * http://developer.android.com/guide/components/bound-services.html
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UsbService.SERVICE_CONNECTED = false;
    }

    /*
     * This function will be called from MainActivity to write data through Serial Port
     */
    public void write(UsbSerialDevice serialPort, byte[] data) {
        serialPort.write(data);
        Log.e("UsbSerialDevice", "sent " + ByteUtil.bytesToHexString(data));
    }

    private void findSerialPortDevice() {
        // This snippet will try to open the first encountered usb device connected, excluding usb root hubs
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                UsbDevice device = entry.getValue();
                Log.e("UsbSevice", "findSerialPortDevice " + device.toString());
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();

                if (deviceVID != 0x1d6b && (devicePID != 0x0001 && devicePID != 0x0002 && devicePID != 0x0003) && deviceVID != 0x5c6 && devicePID != 0x904c) {
                    // There is a device connected to our Android device. Try to open it as a Serial Port.
                    requestUserPermission(device);
                }
            }
        } else {
            // There is no USB devices connected. Send an intent to MainActivity
            Intent intent = new Intent(ACTION_NO_USB);
            sendBroadcast(intent);
            Log.e("TTTTTT", "There is no USB devices connected");
        }
    }

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_DETACHED);
        filter.addAction(ACTION_USB_ATTACHED);
        registerReceiver(usbReceiver, filter);
    }

    int count = 0;
    /*
     * Request user permission. The response will be received in the BroadcastReceiver
     */
    private void requestUserPermission(final UsbDevice device) {
//        if(count>0){
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    PendingIntent mPendingIntent = PendingIntent.getBroadcast(UsbService.this, count++, new Intent(ACTION_USB_PERMISSION), 0);
//                    usbManager.requestPermission(device, mPendingIntent);
//                }
//            }, 1500);
//        }else {
            PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, count++, new Intent(ACTION_USB_PERMISSION), 0);
            usbManager.requestPermission(device, mPendingIntent);
//        }
    }

    public class UsbBinder extends Binder {
        public UsbService getService() {
            return UsbService.this;
        }
    }

    public interface OnUsbReadCallback {
        void onUsbDataReceived(byte[] data);

        void onError(String error);
    }
}
