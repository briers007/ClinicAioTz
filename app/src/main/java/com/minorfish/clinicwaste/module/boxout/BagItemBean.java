package com.minorfish.clinicwaste.module.boxout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: tangjd
 * Date: 2017/6/13
 */

public class BagItemBean {

    /**
     * clinicName : 园区诊所1
     * inTime : 2017-06-13 14:03:49
     * batchNo : 8d2d6e9a73f156e5bf84753451c0ffbb
     * weight : 8.9
     * type : 感染类
     * reason : 异常
     */

    public String clinicName;
    public String inTime;
    public String batchNo;
    public String weight;
    public String type;
    public String reason;
    public String staff;
    public String nurse;
    public String inWeight;

    public static BagItemBean objectFromData(String str) {

        return new Gson().fromJson(str, BagItemBean.class);
    }

    public static BagItemBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), BagItemBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<BagItemBean> arrayBagItemBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<BagItemBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<BagItemBean> arrayBagItemBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<BagItemBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
