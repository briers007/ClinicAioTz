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
 * Date: 2017/6/20
 */

public class BagBean {

    /**
     * trashTypeName : 感染性
     * wardName : B1核医学科
     * weight : 0.73
     * staffTime: 2021-06-10 10:50:20
     */

    public String trashTypeName;
    public String wardName;
    public double weight;
    public String staffTime;

    public static BagBean objectFromData(String str) {

        return new Gson().fromJson(str, BagBean.class);
    }

    public static BagBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), BagBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<BagBean> arrayBagBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<BagBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<BagBean> arrayBagBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<BagBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
