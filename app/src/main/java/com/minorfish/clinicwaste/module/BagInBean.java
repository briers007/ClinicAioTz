package com.minorfish.clinicwaste.module;

import java.io.Serializable;
import java.util.List;

/**
 * Author: tangjd
 * Date: 2017/6/13
 */

public class BagInBean implements Serializable {
    public String bagCode;//溯源码
    public List<String> images;
    public String reason;
    public int status; // 0正常 1异常
    public float weight;
    public long time;

    public String clinicName;
    public String wasteType;
}
