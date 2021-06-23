package com.minorfish.clinicwaste.module.signin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: tangjd
 * Date: 2017/6/8
 */

public class SignInBean {

    /**
     * name : 吴江诊所医废回收站回收员一号
     * phone : 18501540226
     * clinicName : 吴江医废回收站
     * instAddress : null
     * instType : 4
     * principal : null
     * principalPhone : null
     * token : MinorFish eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiI1NCIsImV4cCI6MTU5NjkxMzI5MiwiaWF0IjoxNDk2OTE2MDkyfQ.cBDgQo3E3S1QTtf_LY4yOtmknqNz3UfPkYzv7YLUTo0bVKK2Fzw1bavcTBZxYFV4tHNNwJRlI6nFivgquSpVXw
     */

    public String name;
    public String phone;
    public String instName;
    public String instAddress;
    public int instType;
    public String principal;
    public String principalPhone;
    public String token;

    public static SignInBean objectFromData(String str) {

        return new Gson().fromJson(str, SignInBean.class);
    }

    public static SignInBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), SignInBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<SignInBean> arraySignInBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<SignInBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<SignInBean> arraySignInBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<SignInBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
