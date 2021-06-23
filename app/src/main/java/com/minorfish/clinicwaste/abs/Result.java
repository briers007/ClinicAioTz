package com.minorfish.clinicwaste.abs;

import android.text.TextUtils;

import com.tangjd.common.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/7/8.
 */
public class Result {
    public int mCode = -1;
    public String mMsg;
    public Object mData;
    public String mStatus;

    public static Result parse(JSONObject obj) {
        Result result = new Result();
        if (obj == null) {
            result.mMsg = "数据错误";
            return result;
        }
        result.mData = obj.opt("data");
        result.mCode = obj.optInt("code");
        result.mMsg = obj.optString("message");
        if (TextUtils.isEmpty(result.mMsg) && result.isDataEmpty()) {
            result.mMsg = "没有数据 " + obj.optInt("code");
        }
        return result;
    }

    public static Result parse(String content) {
        Result result = new Result();
        if (StringUtil.isEmpty(content)) {
            result.mMsg = "数据加载失败";
            return result;
        }
        JSONObject obj;
        try {
            obj = new JSONObject(content);
        } catch (JSONException e) {
            e.printStackTrace();
            result.mMsg = "数据错误";
            return result;
        }
        result.mData = obj.opt("data");
        result.mCode = obj.optInt("code");
        result.mMsg = obj.optString("message");
        if (TextUtils.isEmpty(result.mMsg) && result.isDataEmpty()) {
            result.mMsg = "没有数据 " + obj.optInt("code");
        }
        return result;
    }

    public boolean isDataEmpty() {
        return mData == null || mData.toString().length() == 0 || mData.toString().equals("null");
    }

    public boolean isSuccess() {
        return mCode == 200;
    }
}
