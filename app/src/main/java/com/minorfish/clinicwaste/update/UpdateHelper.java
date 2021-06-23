package com.minorfish.clinicwaste.update;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.minorfish.clinicwaste.BaseActivity;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.download.DownloadTask;
import com.tangjd.common.utils.DigestUtils;
import com.tangjd.common.utils.FileUtil;
import com.tangjd.common.utils.StringUtil;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by tangjd on 2017/11/15.
 */

public class UpdateHelper {
    private static boolean isDownload = false;
    /*resp返回
    {"message":"success","status":"OK","result":{"id":64,"osTypeName":"android","description":"","createdAt":"2019-07-23 14:00:01","verName":"v1.0.4","verCode":5,"projectId":44,"osType":1,"projectName":"","url":"http:\/\/clinic-weichat.minorfish.com:91\/3fab597bb852406f9a6faa249310b1f7.apk","must":0,"clientTypeName":"pda","clientType":2},"code":200}
    * */

    public static void checkUpdate(final BaseActivity activity) {
        String url = "http://clinic-weichat.minorfish.com:5321/web/version/manager/lastest/44/1/2.do";
        JsonApiBase.doGetRequest(url, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                UpdateBean updateBean = UpdateBean.objectFromData(response.toString());
                if (updateBean == null) {
                    return;
                }
                if (updateBean.code != 200) {
                    return;
                }
                final UpdateBean.ResultBean bean = updateBean.result;

                if (bean != null) {
                    PackageManager packageManager = activity.getPackageManager();
                    PackageInfo packInfo;
                    try {
                        packInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                    if (bean.verCode > packInfo.versionCode && !StringUtil.isEmpty(bean.url)) {
                        if( !isDownload ) {
                            if (bean.must == 1) {
                                activity.showTipDialog("发现新版本: " + bean.verName + "\n" + bean.description + "\n" + "请升级", false, "升级", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        processUpdate(bean, activity);
                                    }
                                });
                            } else {
                                activity.showAlertDialog("发现新版本: " + bean.verName + "\n" + bean.description + "\n" + "是否现在升级？",
                                        "升级", "取消", false, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                processUpdate(bean, activity);
                                            }
                                        }, null);
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onFinish(boolean withoutException) {

            }
        });
    }


    private static void processUpdate(UpdateBean.ResultBean bean, final BaseActivity activity) {
        isDownload = true;
        final File apkFile = new File(FileUtil.getDownloadDir(activity).getAbsolutePath()
                + File.separator + DigestUtils.md5DigestAsHex(bean.url.getBytes()) + ".apk");
        new Thread(new DownloadTask(bean.url, apkFile, new DownloadTask.DownloadListener() {

            @Override
            public void onUpdate(final long totalSize, final long currentSize, final int percent) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.showProgressDialog("下载中..." + percent + "%");
                    }
                });
            }

            @Override
            public void onComplete(File file) {
                isDownload = false;
                installApk(file, activity);
                activity.dismissProgressDialog();
            }

            @Override
            public void onFailure(final Throwable error) {
                isDownload = false;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.showTipDialog("下载失败: \n" + error.getMessage());
                    }
                });
            }

            @Override
            public void onFileExists(File file) {
                isDownload = false;
                installApk(file, activity);
                activity.dismissProgressDialog();
            }
        })).start();
    }

    private static void installApk(File file, BaseActivity activity) {
        FileUtil.installApk(activity, file, "clinic.fileprovider");
    }
}
