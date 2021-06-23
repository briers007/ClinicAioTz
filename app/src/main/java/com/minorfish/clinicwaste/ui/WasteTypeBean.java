package com.minorfish.clinicwaste.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by tangjd on 2017/2/14.
 */

public class WasteTypeBean implements Serializable {
    public String mName;
    public String mCode;
    public int config;
    public WasteTypeBean children;
    public boolean check = false;

    public static ArrayList<WasteTypeBean> parse(Object data) {
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
        ArrayList<WasteTypeBean> beans = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.optJSONObject(i);
            if (obj == null) {
                continue;
            }
            if(obj.optInt("config", 0) == 1) {
                JSONArray children = obj.optJSONArray("children");
                if(children != null && children.length() > 0) {
                    for(int j = 0; j< children.length(); j++) {
                        JSONObject child = children.optJSONObject(j);
                        if(child == null) continue;

                        if(child.optInt("config") == 1) {
                            WasteTypeBean bean = new WasteTypeBean();
                            bean.mName = child.optString("value");
                            bean.mCode = child.optString("code");
                            bean.config = child.optInt("config");
                            JSONArray children2 = child.optJSONArray("children");
                            if(children2 != null && children2.length() > 0) {
                                JSONObject cc = children2.optJSONObject(0);
                                if(cc.optInt("config") == 1) {
                                    bean.children = new WasteTypeBean();
                                }
                            }
                            beans.add(bean);
                        }
                    }
                }
            }
//            WasteTypeBean bean = new WasteTypeBean();
//            bean.mName = obj.optString("name");
//            bean.mCode = obj.optString("code");
//            beans.add(bean);
        }
        return beans;
    }

    @Override
    public String toString() {
        return mName;
    }
}
