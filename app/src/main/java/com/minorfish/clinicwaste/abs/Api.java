package com.minorfish.clinicwaste.abs;

import android.text.TextUtils;

import com.minorfish.clinicwaste.module.BagInBean;
import com.minorfish.clinicwaste.util.FakeX509TrustManager;
import com.tangjd.common.abs.JsonApiBase;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by tangjd on 2016/8/8.
 */
public class Api {
    private static final String HTTP_SCHEMA;
    private static final String HTTP_SCHEMA_SUFFIX;
    private static final String HOST;

    static {
        HTTP_SCHEMA = "https";
        HTTP_SCHEMA_SUFFIX = "://";
//        HOST = "js-monitor.minorfish.com"; //省平台地址
//        HOST = "192.168.2.171:8014"; //本地调试地址
        HOST = "218.90.225.74:8000"; //泰州平台地址
    }

    public static String getDomainName() {
        return HTTP_SCHEMA + HTTP_SCHEMA_SUFFIX + HOST;
    }

    public static Map<String, String> getHeaders() {
        if (App.getApp().mUserBean == null || TextUtils.isEmpty(App.getApp().mUserBean.token)) {
            return null;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Auth-Token", App.getApp().mUserBean.token);
        return headers;
    }

    public static void login(String nfcId, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/login.do";
        JSONObject params = new JSONObject();
        try {
            params.put("nfccode", nfcId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FakeX509TrustManager.allowAllSSL();
        JsonApiBase.doPostRequest(url, params, getHeaders(), listener);
    }

    public static void login(String userName, String pwd, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/login.do";
        JSONObject params = new JSONObject();
        try {
            params.put("username", userName);
            params.put("password", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FakeX509TrustManager.allowAllSSL();
        JsonApiBase.doPostRequest(url, params, getHeaders(), listener);
    }

    public static void getBagInfoByScan(String scanResult, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/bag/in/detail.do";
        JSONObject params = new JSONObject();
        try {
            params.put("snCode", scanResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FakeX509TrustManager.allowAllSSL();
        JsonApiBase.doPostRequest(url, params, getHeaders(), listener);
    }

    public static void bagIn(List<BagInBean> beans, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/bag/in/batch.do";
        JSONObject params = new JSONObject();
        try {
            JSONArray arr = new JSONArray();
            for (int i = 0; i < beans.size(); i++) {
                BagInBean bean = beans.get(i);
                JSONObject obj = new JSONObject();
                obj.put("bagCode", bean.bagCode);
//                if (bean.images != null && bean.images.size() != 0) {
//                    JSONArray imageArr = new JSONArray();
//                    for (int j = 0; j < bean.images.size(); j++) {
//                        imageArr.put(bean.images.get(j));
//                    }
//                    obj.put("images", imageArr);
//                }
                if (!TextUtils.isEmpty(bean.reason)) {
                    obj.put("reason", bean.reason);
                }
                obj.put("status", bean.status);
                obj.put("weight", bean.weight);
                arr.put(obj);
            }
            params.put("data", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonApiBase.doPostRequest(url, params, getHeaders(), listener);
    }

    public static void getBoxOutData(JsonApiBase.OnJsonResponseListener listener) { //返回值可能不一样
        String url = getDomainName() + "/hw/clinic/pda/bag/out/detail.do";
        JSONObject param = new JSONObject();
        FakeX509TrustManager.allowAllSSL();
        JsonApiBase.doPostRequest(url, param, getHeaders(), listener);
    }

    public static void getBoxOutDataOld(JSONArray checkedTypeCodes,JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/bag/out/detail.do";
        JSONObject param = new JSONObject();
        try {
            param.put("typeCodes", checkedTypeCodes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FakeX509TrustManager.allowAllSSL();
        //Json
        JsonApiBase.doPostRequest(url, param, getHeaders(), listener);
    }


    public static void boxOutOld(String tagId,JSONArray array,JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/bag/out.do";
        JSONObject params = new JSONObject();
        try {
            params.put("nfcCode", tagId);
            params.put("typeCodes", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonApiBase.doPostRequest(url, params, getHeaders(), listener);
    }

    public static void boxOut(String tagId,JSONArray array,JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/bag/out.do";
        JSONObject params = new JSONObject();
        try {
            params.put("nfcCode", tagId);
            params.put("typeCodes", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FakeX509TrustManager.allowAllSSL();
        JsonApiBase.doPostRequest(url, params, getHeaders(), listener);
    }

    public static void getAbnormalData(int currPage, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/record/exception.do";
        JSONObject params = new JSONObject();
        try {
            params.put("page", currPage);
            params.put("size", Constants.ABNORMAL_PAGE_SIZE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FakeX509TrustManager.allowAllSSL();
        JsonApiBase.doPostRequest(url, params, getHeaders(), listener);
    }

    public static void queryOutedInfo(int currPage, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/record/out.do";
        JSONObject params = new JSONObject();
        try {
            params.put("page", currPage);
            params.put("size", Constants.OUTED_PAGE_SIZE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonApiBase.doPostRequest(url, params, getHeaders(), listener);
    }

    public static void queryOutedInfoV2(int currPage, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/record/out/v2.do";
        JSONObject params = new JSONObject();
        try {
            params.put("page", currPage);
            params.put("size", Constants.OUTED_PAGE_SIZE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FakeX509TrustManager.allowAllSSL();
        JsonApiBase.doPostRequest(url, params, getHeaders(), listener);
    }

    public static void getBoxDetail(int page, String id, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/record/out/detail.do";
        JSONObject params = new JSONObject();
        try {
            params.put("page", page);
            params.put("id", id);
            params.put("size", Constants.OUTED_BOX_DETAIL_SIZE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FakeX509TrustManager.allowAllSSL();
        JsonApiBase.doPostRequest(url, params, getHeaders(), listener);
    }

    public static void uploadPhoto(List<String> photos, StringCallback callback) {
        Map<String, String> params = new HashMap<>();
        String url = getDomainName() + "/api/file/upload/v2.do";
        PostFormBuilder builder = OkHttpUtils.post();
        for (int i = 0; i < photos.size(); i++) {
            String uuid = UUID.randomUUID().toString();
            builder.addFile(uuid, uuid + ".jpg", new File(photos.get(i)));
        }
        FakeX509TrustManager.allowAllSSL();
        builder.url(url).params(params);
        builder.build()
                .connTimeOut(5000)
                .writeTimeOut(30000)
                .readTimeOut(30000)
                .execute(callback);
    }

    public static void modifyPwd(String oldPwd, String newPwd, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/user/changepwd.do";
        JSONObject params = new JSONObject();
        try {
            params.put("old_password", oldPwd);
            params.put("new_password", newPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FakeX509TrustManager.allowAllSSL();
        JsonApiBase.doPostRequest(url, params, getHeaders(), listener);
    }



    public static void getWasteType(JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/type/list.do";
        FakeX509TrustManager.allowAllSSL();
        JsonApiBase.doGetRequest(url, null, getHeaders(), listener);
        //enqueue(sApiServiceGet.getWasteType(getHeaders()), listener);
    }

    public static void getCompanyList(JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/bag/out/company/list.do";
        FakeX509TrustManager.allowAllSSL();
        JsonApiBase.doGetRequest(url, null, getHeaders(), listener);
        //enqueue(sApiServiceGet.getCompanyList(getHeaders()), listener);
    }

    public static void getSourceCode(Map<String,String> code,JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/clinic/pda/sourceCode/validation.do";
        FakeX509TrustManager.allowAllSSL();
        JsonApiBase.doGetRequest(url, code, getHeaders(), listener);
    }

}
