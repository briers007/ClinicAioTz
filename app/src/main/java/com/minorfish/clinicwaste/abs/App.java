package com.minorfish.clinicwaste.abs;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.minorfish.clinicwaste.module.signin.SignInBean;
import com.minorfish.clinicwaste.util.CrashHandler;
import com.tangjd.common.manager.SPManager;
import com.tangjd.common.manager.VolleyManager;
import com.tangjd.common.utils.Log;
import com.umeng.commonsdk.UMConfigure;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Administrator on 2016/7/2.
 */
public class App extends Application {
    private static App sApp;

    public Handler mBackHandler;
    public HandlerThread mHandlerThread;

    public SignInBean mUserBean;
    public String mToken;

    public synchronized static App getApp() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        Log.setLoggable(Configs.LOGABLE);
        SPManager.getInstance().init(this);
        VolleyManager.getInstance().init(this);

        handleSSLHandshake();

        mHandlerThread = new HandlerThread("AppBackHandlerThread");
        mHandlerThread.start();
        mBackHandler = new Handler(mHandlerThread.getLooper());

        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "5b96053b8f4a9d29ab000401");

        CrashHandler.getInstance().init(getApplicationContext());
    }

    public void signOut() {
        SPManager.getInstance().putString(Constants.PREF_KEY_SIGN_IN_BEAN, null);
        mUserBean = null;
        mToken = null;
    }

    public void setSignInBean(String data, SignInBean bean) {
        SPManager.getInstance().putString(Constants.PREF_KEY_SIGN_IN_BEAN, data);
        mUserBean = bean;
    }

    public SignInBean getSignInBean() {
        if (mUserBean == null) {
            String data = SPManager.getInstance().getString(Constants.PREF_KEY_SIGN_IN_BEAN, null);
            mUserBean = SignInBean.objectFromData(data);
            if (mUserBean == null) {
                return null;
            }
            if (TextUtils.isEmpty(mUserBean.token)) {
                return null;
            }
            mToken = mUserBean.token;
        }
        return mUserBean;
    }


    public void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
