package com.minorfish.clinicwaste.module.boxout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Chris
 * Date: 2018/8/21
 */

public class CompanyBean {


    public String companyId;
    public String companyName;

    public static CompanyBean objectFromData(String str) {

        return new Gson().fromJson(str, CompanyBean.class);
    }

    public static List<CompanyBean> arrayCompanyBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<CompanyBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    @Override
    public String toString() {
        return companyName;
    }
}
