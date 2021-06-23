package com.minorfish.clinicwaste.module.query.boxdetail;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: tangjd
 * Date: 2017/6/16
 */

public class BagInBoxBean {

    /**
     * clinicName : 园区诊所1
     * batchNo : 8d2d6e9a73f156e5bf84753451c0ffbe
     * weight : 0.27
     * type : 感染类
     * time : 2017-06-14 14:11:22
     */

    public String clinicName;
    public String batchNo;
    public String weight;
    public String type;
    public String time;

    public static BagInBoxBean objectFromData(String str) {

        return new Gson().fromJson(str, BagInBoxBean.class);
    }

    public static BagInBoxBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), BagInBoxBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<BagInBoxBean> arrayBagInBoxBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<BagInBoxBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<BagInBoxBean> arrayBagInBoxBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<BagInBoxBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
