package com.minorfish.clinicwaste.module.bagin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: tangjd
 * Date: 2017/6/9
 */

public class CheckBagBean implements Serializable {

    /**
     * clinicName : 吴江诊所
     * outTime : 2017-05-25 18:11:40
     * batchNo : null
     * type : 感染性
     */

    public String clinicName;
    public String outTime;
    public String batchNo;
    public String type;

    public static CheckBagBean objectFromData(String str) {
        return new Gson().fromJson(str, CheckBagBean.class);
    }

    public static CheckBagBean objectFromData(String str, String key) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            return new Gson().fromJson(jsonObject.getString(str), CheckBagBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<CheckBagBean> arrayCheckBagBeanFromData(String str) {
        Type listType = new TypeToken<ArrayList<CheckBagBean>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

    public static List<CheckBagBean> arrayCheckBagBeanFromData(String str, String key) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<CheckBagBean>>() {
            }.getType();
            return new Gson().fromJson(jsonObject.getString(str), listType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList();
    }
}
