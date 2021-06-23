package com.minorfish.clinicwaste.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Spinner;

import com.acs.smartcard.Reader;
import com.minorfish.clinicwaste.abs.App;

/**
 * Author: Administrator
 * Date: 2018/3/21
 */

public class NfcHelper {
    private Reader mReader;
    private static NfcHelper sHelper;

    public static NfcHelper getInstance() {
        if (sHelper == null) {
            sHelper = new NfcHelper(App.getApp().getApplicationContext());
        }
        return sHelper;
    }

    private NfcHelper(Context context) {
        mReader = new Reader((UsbManager) context.getSystemService(Context.USB_SERVICE));
    }

    public void connect(final UsbDevice device) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mReader.open(device);
                    Thread.sleep(3000);
                    int status = mReader.getState(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getTagId() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    processOnBtnPower();
                    processOnBtnSetProtocol();
                    processOnBtnTransmit();
                } catch (Exception e) {

                }
            }
        }).start();
    }

    public boolean processOnBtnPower() {
        // Get slot number
        int slotNum = mReader.getNumSlots();
        if (slotNum > 0) {
            slotNum = 0;
        }
        // Get action number
        int actionNum = Reader.CARD_WARM_RESET;
        // If slot and action are selected
        if (slotNum != Spinner.INVALID_POSITION && actionNum != Spinner.INVALID_POSITION) {
            if (actionNum < Reader.CARD_POWER_DOWN || actionNum > Reader.CARD_WARM_RESET) {
                actionNum = Reader.CARD_WARM_RESET;
            }
            try {
                mReader.power(slotNum, actionNum);
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public boolean processOnBtnSetProtocol() {
        int preferredProtocols = Reader.PROTOCOL_UNDEFINED;
        preferredProtocols |= Reader.PROTOCOL_T0;
        preferredProtocols |= Reader.PROTOCOL_T1;
        try {
            mReader.setProtocol(0, preferredProtocols);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void processOnBtnTransmit() {
        // Get slot number
        byte[] command = new byte[]{(byte) 0xFF, (byte) 0xCA, 0x00, 0x00, 0x00};
        byte[] response = new byte[300];
        try {
            int responseLength = mReader.transmit(0, command, command.length, response, response.length);
            if (responseLength > 0) {
                mUsbReadCallback.onUsbDataReceived(response);
            } else {
                mUsbReadCallback.onError("没有数据");
            }
        } catch (Exception e) {
            mUsbReadCallback.onError(e.getMessage());
        }
    }


    private String buffer2Str(byte[] buffer) {
        String bufferString = "";
        for (int i = 0; i < buffer.length; i++) {
            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }
            bufferString += hexChar.toUpperCase();
        }
        return bufferString;
    }

    public void deviceDetached() {

    }

    private UsbService.OnUsbReadCallback mUsbReadCallback;

    public void setOnUsbReadCallback(UsbService.OnUsbReadCallback usbReadCallback) {
        mUsbReadCallback = usbReadCallback;
    }

    private class TransmitProgress {

        public int controlCode;
        public byte[] command;
        public int commandLength;
        public byte[] response;
        public int responseLength;
        public Exception e;
    }

    /**
     * Converts the HEX string to byte array.
     *
     * @param hexString the HEX string.
     * @return the byte array.
     */
    private byte[] toByteArray(String hexString) {

        int hexStringLength = hexString.length();
        byte[] byteArray = null;
        int count = 0;
        char c;
        int i;

        // Count number of hex characters
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f') {
                count++;
            }
        }

        byteArray = new byte[(count + 1) / 2];
        boolean first = true;
        int len = 0;
        int value;
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }

            if (value >= 0) {

                if (first) {

                    byteArray[len] = (byte) (value << 4);

                } else {

                    byteArray[len] |= value;
                    len++;
                }

                first = !first;
            }
        }

        return byteArray;
    }

}
