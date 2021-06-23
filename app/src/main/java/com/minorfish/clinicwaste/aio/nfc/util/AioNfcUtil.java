package com.minorfish.clinicwaste.aio.nfc.util;

import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.aio.nfc.helper.AioNfcHelper;

/**
 * Author: Administrator
 * Date: 2018/3/12
 */

public class AioNfcUtil {
    public BaseActivity mActivity;
    private AioNfcHelper mHelper;
    public static final String[] stateStrings = {"Unknown", "Absent",
            "Present", "Swallowed", "Powered", "Negotiable", "Specific"};

    public AioNfcUtil(BaseActivity activity) {
        this.mActivity = activity;
        mHelper = new AioNfcHelper(activity);
        // Step
        // 1.Open serial port
        // 2.Power slot
        // 3.Set Protocol T = 0, T = 1 (ignore?)
        // 4.Transmit (FF CA 00 00 00)

        // State 1.absent 2.present
        mHelper.setOnNfcCardAttachListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(String currState) {
                if (currState.equals(stateStrings[0])) {
                } else if (currState.equals(stateStrings[1])) {
                } else if (currState.equals(stateStrings[2])) {
                    // TODO press power button
                } else if (currState.equals(stateStrings[3])) {
                } else if (currState.equals(stateStrings[4])) {
                } else if (currState.equals(stateStrings[5])) {
                } else if (currState.equals(stateStrings[6])) {
                }
            }
        });
    }

    public interface OnStateChangeListener {
        void onStateChange(String currState);
    }
}
