package com.minorfish.clinicwaste.update;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Administrator
 * Date: 2017/11/1
 */

public class UpdateBean {

    /**
     * code : 200
     * message : success
     * result : {"clientType":1,"clientTypeName":"app","createdAt":"2017-11-14 18:21:48","description":"1.修复部分问题","id":6,"must":0,"osType":1,"osTypeName":"android","projectId":9,"projectName":"","url":"http://clinic-weichat.minorfish.com:91/265c2a99bede4c9895a4edde3332f7d2.apk","verCode":1000,"verName":"v1.0"}
     * status : OK
     */

    public int code;
    public String message;
    public ResultBean result;
    public String status;

    public static UpdateBean objectFromData(String str) {
        return new Gson().fromJson(str, UpdateBean.class);
    }

    public static List<UpdateBean> arrayFromData(String str) {
        Type listType = new TypeToken<ArrayList<UpdateBean>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

    public static class ResultBean {
        /**
         * clientType : 1
         * clientTypeName : app
         * createdAt : 2017-11-14 18:21:48
         * description : 1.修复部分问题
         * id : 6
         * must : 0
         * osType : 1
         * osTypeName : android
         * projectId : 9
         * projectName :
         * url : http://clinic-weichat.minorfish.com:91/265c2a99bede4c9895a4edde3332f7d2.apk
         * verCode : 1000
         * verName : v1.0
         */

        public int clientType;
        public String clientTypeName;
        public String createdAt;
        public String description;
        public int id;
        public int must;
        public int osType;
        public String osTypeName;
        public int projectId;
        public String projectName;
        public String url;
        public int verCode;
        public String verName;

        public static ResultBean objectFromData(String str) {
            return new Gson().fromJson(str, ResultBean.class);
        }

        public static List<ResultBean> arrayFromData(String str) {
            Type listType = new TypeToken<ArrayList<ResultBean>>() {
            }.getType();
            return new Gson().fromJson(str, listType);
        }
    }
}
