package com.minorfish.clinicwaste.module.query;

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
 * Date: 2017/6/16
 */

public class OutedBoxBean implements Serializable {

    public String id;
    public String receiveTime;
    //    public int damageBags;
//    public String damageWeight;
//    public int infectBags;
//    public String infectWeight;
//    public int bloodBags;
//    public String bloodWeight;
    public int qty;
    public String weight;
    public String typeName;

    public String receiveCompanyName;

    public static OutedBoxBean objectFromData(String str) {
        return new Gson().fromJson(str, OutedBoxBean.class);
    }

    public static OutedBoxBean objectFromData(String str, String key) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            return new Gson().fromJson(jsonObject.getString(str), OutedBoxBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<OutedBoxBean> arrayOutedBoxBeanFromData(String str) {
        Type listType = new TypeToken<ArrayList<OutedBoxBean>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

    public static List<OutedBoxBean> arrayOutedBoxBeanFromData(String str, String key) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<OutedBoxBean>>() {
            }.getType();
            return new Gson().fromJson(jsonObject.getString(str), listType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList();
    }
}
