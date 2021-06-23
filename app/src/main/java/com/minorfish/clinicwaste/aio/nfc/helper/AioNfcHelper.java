/*
 * Copyright (C) 2011-2013 Advanced Card Systems Ltd. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Advanced
 * Card Systems Ltd. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with ACS.
 */

package com.minorfish.clinicwaste.aio.nfc.helper;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.widget.Spinner;

import com.acs.smartcard.Features;
import com.acs.smartcard.PinModify;
import com.acs.smartcard.PinVerify;
import com.acs.smartcard.ReadKeyOption;
import com.acs.smartcard.Reader;
import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.aio.nfc.util.AioNfcUtil;
import com.tangjd.common.utils.Log;

/**
 * Test program for ACS smart card readers.
 *
 * @author Godfrey Chung
 * @version 1.1.1, 16 Apr 2013
 */
public class AioNfcHelper {

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private static final String[] powerActionStrings = {"Power Down",
            "Cold Reset", "Warm Reset"};

    public static final String[] stateStrings = {"Unknown", "Absent",
            "Present", "Swallowed", "Powered", "Negotiable", "Specific"};

    private static final String[] featureStrings = {"FEATURE_UNKNOWN",
            "FEATURE_VERIFY_PIN_START", "FEATURE_VERIFY_PIN_FINISH",
            "FEATURE_MODIFY_PIN_START", "FEATURE_MODIFY_PIN_FINISH",
            "FEATURE_GET_KEY_PRESSED", "FEATURE_VERIFY_PIN_DIRECT",
            "FEATURE_MODIFY_PIN_DIRECT", "FEATURE_MCT_READER_DIRECT",
            "FEATURE_MCT_UNIVERSAL", "FEATURE_IFD_PIN_PROPERTIES",
            "FEATURE_ABORT", "FEATURE_SET_SPE_MESSAGE",
            "FEATURE_VERIFY_PIN_DIRECT_APP_ID",
            "FEATURE_MODIFY_PIN_DIRECT_APP_ID", "FEATURE_WRITE_DISPLAY",
            "FEATURE_GET_KEY", "FEATURE_IFD_DISPLAY_PROPERTIES",
            "FEATURE_GET_TLV_PROPERTIES", "FEATURE_CCID_ESC_COMMAND"};

    private static final String[] propertyStrings = {"Unknown", "wLcdLayout",
            "bEntryValidationCondition", "bTimeOut2", "wLcdMaxCharacters",
            "wLcdMaxLines", "bMinPINSize", "bMaxPINSize", "sFirmwareID",
            "bPPDUSupport", "dwMaxAPDUDataSize", "wIdVendor", "wIdProduct"};

    private static final int DIALOG_VERIFY_PIN_ID = 0;
    private static final int DIALOG_MODIFY_PIN_ID = 1;
    private static final int DIALOG_READ_KEY_ID = 2;
    private static final int DIALOG_DISPLAY_LCD_MESSAGE_ID = 3;

    private UsbManager mManager;
    private Reader mReader;
    private PendingIntent mPermissionIntent;

    private static final int MAX_LINES = 25;

    private Features mFeatures = new Features();
    private PinVerify mPinVerify = new PinVerify();
    private PinModify mPinModify = new PinModify();
    private ReadKeyOption mReadKeyOption = new ReadKeyOption();
    private String mLcdMessage;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {

                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // Open reader
                            logMsg("Opening reader: " + device.getDeviceName() + "...");
                            new OpenTask().execute(device);
                        }
                    } else {
                        logMsg("Permission denied for device " + device.getDeviceName());
                        // Enable open button
//                        mOpenButton.setEnabled(true);
                    }
                }

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                synchronized (this) {
                    new CloseTask().execute();
                }
            }
        }
    };

    private class OpenTask extends AsyncTask<UsbDevice, Void, Exception> {
        @Override
        protected Exception doInBackground(UsbDevice... params) {
            Exception result = null;
            try {
                mReader.open(params[0]);
            } catch (Exception e) {
                result = e;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                logMsg(result.toString());
            } else {
                logMsg("Reader name: " + mReader.getReaderName());
                int numSlots = mReader.getNumSlots();
                logMsg("Number of slots: " + numSlots);
                // Remove all control codes
                mFeatures.clear();
            }
        }
    }

    private class CloseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mReader.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    public class PowerParams {
        public int slotNum;
        public int action;
    }

    private class PowerResult {

        public byte[] atr;
        public Exception e;
    }

    private class SetProtocolParams {

        public int slotNum;
        public int preferredProtocols;
    }

    private class SetProtocolResult {

        public int activeProtocol;
        public Exception e;
    }

    private class TransmitParams {
        public int slotNum;
        public int controlCode;
        public String commandString;
    }

    private class TransmitProgress {

        public int controlCode;
        public byte[] command;
        public int commandLength;
        public byte[] response;
        public int responseLength;
        public Exception e;
    }

    private BaseActivity mActivity;

    public AioNfcHelper(BaseActivity activity) {
        mActivity = activity;
        // Get USB manager
        mManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);

        // Initialize reader
        mReader = new Reader(mManager);
        mReader.setOnStateChangeListener(new Reader.OnStateChangeListener() {

            @Override
            public void onStateChange(int slotNum, int prevState, int currState) {

                if (prevState < Reader.CARD_UNKNOWN || prevState > Reader.CARD_SPECIFIC) {
                    prevState = Reader.CARD_UNKNOWN;
                }

                if (currState < Reader.CARD_UNKNOWN || currState > Reader.CARD_SPECIFIC) {
                    currState = Reader.CARD_UNKNOWN;
                }

                // Create output string
                final String outputString = "Slot " + slotNum + ": " + stateStrings[prevState] + " -> " + stateStrings[currState];
                // Show output
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        logMsg(outputString);
                    }
                });

                mOnStateChangeListener.onStateChange(stateStrings[currState]);
            }
        });

        // Register receiver for USB permission
        mPermissionIntent = PendingIntent.getBroadcast(mActivity, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        activity.registerReceiver(mReceiver, filter);


        // Initialize LCD message
        mLcdMessage = "Hello!";
    }

    protected void onDestroy() {
        // Close reader
        mReader.close();
        // Unregister receiver
        mActivity.unregisterReceiver(mReceiver);
    }


    /**
     * Logs the message.
     *
     * @param msg the message.
     */
    private void logMsg(String msg) {
        Log.e("TTTTTT_NFC", msg);
    }

    /**
     * Logs the contents of buffer.
     *
     * @param buffer       the buffer.
     * @param bufferLength the buffer length.
     */
    private void logBuffer(byte[] buffer, int bufferLength) {
        String bufferString = "";
        for (int i = 0; i < bufferLength; i++) {
            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }
            if (i % 16 == 0) {
                if (bufferString != "") {
                    logMsg(bufferString);
                    bufferString = "";
                }
            }

            bufferString += hexChar.toUpperCase() + " ";
        }

        if (bufferString != "") {
            logMsg(bufferString);
        }
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

    /**
     * Converts the integer to HEX string.
     *
     * @param i the integer.
     * @return the HEX string.
     */
    private String toHexString(int i) {

        String hexString = Integer.toHexString(i);
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }

        return hexString.toUpperCase();
    }

    /**
     * Converts the byte array to HEX string.
     *
     * @param buffer the buffer.
     * @return the HEX string.
     */
    private String toHexString(byte[] buffer) {

        String bufferString = "";

        for (int i = 0; i < buffer.length; i++) {

            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }

            bufferString += hexChar.toUpperCase() + " ";
        }

        return bufferString;
    }

    private AioNfcUtil.OnStateChangeListener mOnStateChangeListener;

    public void setOnNfcCardAttachListener(AioNfcUtil.OnStateChangeListener listener) {
        mOnStateChangeListener = listener;
    }

    public boolean processOnBtnOpen() {
        boolean requested = false;
        // For each device
        for (UsbDevice device : mManager.getDeviceList().values()) {
            // If device name is found
            mManager.requestPermission(device, mPermissionIntent);
            requested = true;
            break;
        }
        return requested;
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
            // Set parameters
            PowerParams params = new PowerParams();
            params.slotNum = slotNum;
            params.action = actionNum;
            // Perform power action
            logMsg("Slot " + slotNum + ": " + powerActionStrings[actionNum] + "...");

            PowerResult result = new PowerResult();
            try {
                result.atr = mReader.power(params.slotNum, params.action);
                return true;
            } catch (Exception e) {
                result.e = e;
            }
        }
        return false;
    }

    public boolean processOnBtnSetProtocol() {
        // Get slot number
        int slotNum = mReader.getNumSlots();
        if (slotNum > 0) {
            slotNum = 0;
        }
        // If slot is selected
        if (slotNum != Spinner.INVALID_POSITION) {
            int preferredProtocols = Reader.PROTOCOL_UNDEFINED;
            String preferredProtocolsString = "";
            preferredProtocols |= Reader.PROTOCOL_T0;
            preferredProtocolsString = "T=0";

            preferredProtocols |= Reader.PROTOCOL_T1;
            if (preferredProtocolsString != "") {
                preferredProtocolsString += "/";
            }
            preferredProtocolsString += "T=1";

            if (preferredProtocolsString == "") {
                preferredProtocolsString = "None";
            }
            // Set Parameters
            SetProtocolParams params = new SetProtocolParams();
            params.slotNum = slotNum;
            params.preferredProtocols = preferredProtocols;
            // Set protocol
            logMsg("Slot " + slotNum + ": Setting protocol to " + preferredProtocolsString + "...");

            SetProtocolResult result = new SetProtocolResult();
            try {
                result.activeProtocol = mReader.setProtocol(params.slotNum, params.preferredProtocols);
                return true;
            } catch (Exception e) {
                result.e = e;
            }
        }
        return false;
    }

    public String processOnBtnTransmit() {
        // Get slot number
        int slotNum = mReader.getNumSlots();
        if (slotNum > 0) {
            slotNum = 0;
        }
        // If slot is selected
        if (slotNum != Spinner.INVALID_POSITION) {
            // Set parameters
            TransmitParams params = new TransmitParams();
            params.slotNum = slotNum;
            params.controlCode = -1;
            params.commandString = "FF CA 00 00 00";
            // Transmit APDU
            logMsg("Slot " + slotNum + ": Transmitting APDU...");

            TransmitProgress progress = null;
            byte[] command = null;
            byte[] response = null;
            int responseLength = 0;
            int foundIndex = 0;
            int startIndex = 0;
            do {
                // Find carriage return
                foundIndex = params.commandString.indexOf('\n', startIndex);
                if (foundIndex >= 0) {
                    command = toByteArray(params.commandString.substring(startIndex, foundIndex));
                } else {
                    command = toByteArray(params.commandString.substring(startIndex));
                }
                // Set next start index
                startIndex = foundIndex + 1;
                response = new byte[300];
                progress = new TransmitProgress();
                progress.controlCode = params.controlCode;
                try {
                    if (params.controlCode < 0) {
                        // Transmit APDU
                        responseLength = mReader.transmit(params.slotNum, command, command.length, response, response.length);
                    } else {
                        // Transmit control command
                        responseLength = mReader.control(params.slotNum, params.controlCode, command, command.length, response, response.length);
                    }
                    progress.command = command;
                    progress.commandLength = command.length;
                    progress.response = response;
                    progress.responseLength = responseLength;
                    progress.e = null;
                } catch (Exception e) {
                    progress.command = null;
                    progress.commandLength = 0;
                    progress.response = null;
                    progress.responseLength = 0;
                    progress.e = e;
                }

                if (progress.e != null) {
                    logMsg(progress.e.toString());
                } else {
                    logMsg("Command:");
                    logBuffer(progress.command, progress.commandLength);
                    logMsg("Response:");
                    logBuffer(progress.response, progress.responseLength);
                    if (progress.response != null && progress.responseLength > 0) {
                        return buffer2Str(progress.response);
                    }
                }
            } while (foundIndex >= 0);
        }
        return null;
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
}
