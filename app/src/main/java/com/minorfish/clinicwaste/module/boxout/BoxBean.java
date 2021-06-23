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

public class BoxBean {

    /**
     * instName : 苏州科技城医院
     * totalNum : 2
     * totalWeight : 1.27
     * type1Num : 1
     * type1Weight : 0.73
     * type2Num : 0
     * type2Weight : 0
     * type3Num : 1
     * type3Weight : 0.54
     * type4Num : 0
     * type4Weight : 0
     * type5Num : 0
     * type5Weight : 0
     */

    public String instName;
    public int totalNum;
    public double totalWeight;
    public int type1Num;
    public double type1Weight;
    public boolean type1Show;
    public int type2Num;
    public double type2Weight;
    public boolean type2Show;
    public int type3Num;
    public double type3Weight;
    public boolean type3Show;
    public int type4Num;
    public double type4Weight;
    public boolean type4Show;
    public int type5Num;
    public double type5Weight;
    public boolean type5Show;
    public int type6Num;
    public double type6Weight;
    public boolean type6Show;
    public int type7Num;
    public double type7Weight;
    public boolean type7Show;

    public static BoxBean objectFromData(String str) {

        return new Gson().fromJson(str, BoxBean.class);
    }

    public static BoxBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), BoxBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<BoxBean> arrayBoxBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<BoxBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<BoxBean> arrayBoxBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<BoxBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
