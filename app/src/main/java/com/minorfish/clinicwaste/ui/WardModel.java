package com.minorfish.clinicwaste.ui;

import android.text.TextUtils;

import java.util.List;

//科室
public class WardModel {
    public String hospitalName; //医院名称
    public String keShiName; //科室名称
    public String nurseName; //护士名称

    public String wardId; //科室ID
    public String nurseId; //护士ID ,
    public String nfcCode = ""; //科室绑定NFC的code ,
    public String nurseSignPic = ""; //护士签名图片（附一院使用） ,
    public List<WasteModel> trashes; // (Array[垃圾Condition], optional): 医废详情 ,

    public boolean isChecked() {
        return !TextUtils.isEmpty(hospitalName)
                && !TextUtils.isEmpty(keShiName)
                && !TextUtils.isEmpty(nurseName)
                && !TextUtils.isEmpty(wardId)
                && !TextUtils.isEmpty(nurseId);
    }
}
