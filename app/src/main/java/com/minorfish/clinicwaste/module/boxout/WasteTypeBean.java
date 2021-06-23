package com.minorfish.clinicwaste.module.boxout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangjd on 2017/2/14.
 */

public class WasteTypeBean implements Serializable {
    public String mName;
    public int mCode;
    public boolean check = false;

    public static List<WasteTypeBean> parse(Object data) {
        JSONArray arr;
        try {
            arr = (JSONArray) data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (arr == null) {
            return null;
        }
        List<WasteTypeBean> beans = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.optJSONObject(i);
            if (obj == null) {
                continue;
            }
            WasteTypeBean bean = new WasteTypeBean();
            bean.mName = obj.optString("name");
            bean.mCode = obj.optInt("code");
            beans.add(bean);
        }
        return beans;
    }

    @Override
    public String toString() {
        return mName;
    }
}
