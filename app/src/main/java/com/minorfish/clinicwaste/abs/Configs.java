package com.minorfish.clinicwaste.abs;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/2.
 */
public class Configs {
    public static final int RSST_ULTIMATE_VALUE = -50;

    public static final String[] IN_FAIL_REASON = new String[]{"垃圾信息不符", "重量误差超过警戒线"};
    public static final String[] SCAN_TITLE = new String[]{"入库扫描", "出库扫描", "入库垃圾箱扫描"};

    public static final boolean LOGABLE = true;

    public static final String OUT_SUCCESS = "出库成功\n";
    public static final String OUT_FAIL = "出库失败\n";
    public static final String IN_SUCCESS = "入库成功\n";
    public static final String IN_FAIL = "入库失败\n";

    private static final List<String> TRUSTED_MAC_LIST = new ArrayList<>();

    static {
        // TRUSTED_MAC_LIST.add("19:18:FC:01:73:91");
        TRUSTED_MAC_LIST.add("D4:22:01:00:05:7D");
        TRUSTED_MAC_LIST.add("D4:22:01:00:04:B2");
        TRUSTED_MAC_LIST.add("D4:22:01:00:04:B3");
        TRUSTED_MAC_LIST.add("D4:22:01:00:04:B8");
        TRUSTED_MAC_LIST.add("D4:22:01:00:09:E1");
    }

    public static boolean filterMac(String macAddress) {
        return !TextUtils.isEmpty(macAddress) && TRUSTED_MAC_LIST.contains(macAddress);
    }
}
