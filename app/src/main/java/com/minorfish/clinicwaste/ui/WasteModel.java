package com.minorfish.clinicwaste.ui;



import java.io.Serializable;

// 垃圾
public class WasteModel implements Serializable {
    // 垃圾编号
    public String mId;
    public String name;
    // 添加时间
    public long mCreateTime;
//    public WasteType mWasteTypeBean;
    // 类型
    public WasteTypeBean mWasteTypeBean2;
    public String mWeight = "";
//    public WardBean mWardBean;

    public int count = 0;
    public int placenta = 0;    // 胎盘

    public int specialType = 0; // 0非，1新冠


}
