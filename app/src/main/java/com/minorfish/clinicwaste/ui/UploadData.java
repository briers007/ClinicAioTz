package com.minorfish.clinicwaste.ui;

import android.graphics.Bitmap;


import java.util.ArrayList;
import java.util.List;

public class UploadData {

    private static UploadData instance = new UploadData();
    // 扫描到的垃圾列表
    public List<WasteModel> mAddedWasteBeans = new ArrayList<>();
    // 科室
    public WardModel keshi = new WardModel();
    // 扎带扫描
    public WasteModel mWaste = new WasteModel();
    // 扎带类型
    public List<WasteTypeBean> typeList = new ArrayList<>();
//    // 交接人
//    public WardBean jiaojie = new WardBean();

    public Bitmap ercode;
    public String qrcode;
    public Bitmap printBitmap;

    public float totalWeight = 0;
    public float zeroWeight = 0;

    private UploadData() {}

    public static UploadData getInstance() {

        return instance;
    }

    public float carWeigth() {
        return totalWeight + zeroWeight;
    }

    public boolean isReUp() {
        if(keshi != null && keshi.isChecked() && mAddedWasteBeans != null && !mAddedWasteBeans.isEmpty()) {
            return false;
        }
        return true;
    }

    // 重新扫描扎带。 科室、扎带列表信息保留
    public void reScan() {
        mWaste = new WasteModel();
        keshi.trashes = null;
        if (ercode != null) {
            ercode.recycle();
            ercode = null;
        }
        if (printBitmap != null) {
            printBitmap.recycle();
            printBitmap = null;
        }
    }

    //称重
    public void doWeight() {
        if(mAddedWasteBeans != null && mAddedWasteBeans.size() > 0) {
            totalWeight = 0;
            for(WasteModel m : mAddedWasteBeans) {
                try {
                    totalWeight += Float.valueOf(m.mWeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            totalWeight = 0f;
        }
    }

    public void clear() {
        try {
            mAddedWasteBeans = new ArrayList<>();
            mWaste = new WasteModel();
            keshi = new WardModel();
            typeList.clear();
            zeroWeight = 0;
            if (ercode != null) {
                ercode.recycle();
                ercode = null;
            }
            if (printBitmap != null) {
                printBitmap.recycle();
                printBitmap = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
