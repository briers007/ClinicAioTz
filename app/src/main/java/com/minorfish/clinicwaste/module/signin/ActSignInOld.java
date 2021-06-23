package com.minorfish.clinicwaste.module.signin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.Api;
import com.minorfish.clinicwaste.abs.App;
import com.minorfish.clinicwaste.abs.Result;
import com.minorfish.clinicwaste.module.frame.ActFrame;
import com.minorfish.clinicwaste.usb.NfcHelper;
import com.minorfish.clinicwaste.usb.UsbService;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.ByteUtil;

import org.json.JSONObject;

/**
 * Created by tangjd on 2016/8/8.
 */
public class ActSignInOld extends SignInHelper {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.act_sign_in_layout);
        initView();

        NfcHelper.getInstance().setOnUsbReadCallback(new UsbService.OnUsbReadCallback() {
            @Override
            public void onUsbDataReceived(byte[] data) {
                //Log.e("TTTTTT", ByteUtil.bytesToHexStringWithoutSpace(data));
                String tagId = ByteUtil.bytesToHexStringWithoutSpace(ByteUtil.subByteArr(data, 0, 4));
                showProgressDialog();
            }

            @Override
            public void onError(String error) {
                showTipDialog(error);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (App.getApp().getSignInBean() != null && !TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
            ActFrame.startActivity(ActSignInOld.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.getApp().getSignInBean() != null && !TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
            ActFrame.startActivity(ActSignInOld.this);
        }
    }

    @Override
    public void processLogin(String tagId) {
        showProgressDialog();
        Api.login(tagId, new JsonApiBase.OnJsonResponseListener() {

            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    SignInBean bean = SignInBean.objectFromData(result.mData + "");
                    if (bean == null || TextUtils.isEmpty(bean.token)) {
                        onError("数据出错");
                        return;
                    }
                    showShortToast("登录成功");
                    App.getApp().setSignInBean(result.mData + "", bean);
                    App.getApp().mToken = bean.token;
                    ActFrame.startActivity(ActSignInOld.this);
                    finish();
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String error) {
                showTipDialog(error + "");
            }

            @Override
            public void onFinish(boolean withoutException) {
                dismissProgressDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        enableMenu(menu, "帐号登录", new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                ActSignInPwd.startActivity(ActSignIn.this);
//                finish();
//                return false;
//            }
//        });
        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        setToolbarTitle("登录");
        findViewById(R.id.toolbar).setBackgroundColor(Color.TRANSPARENT);

        ImageView ivAnim1 = (ImageView) findViewById(R.id.iv_anim_1);
        ImageView ivAnim2 = (ImageView) findViewById(R.id.iv_anim_2);
        final ImageView ivAnim3 = (ImageView) findViewById(R.id.iv_anim_3);

        // rotate animation of ivAnim1
        RotateAnimation rotateAnimation = new RotateAnimation(0, 359, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        // 单次动画时间
        rotateAnimation.setDuration(6000);
        // 无限循环
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        // 设置不停顿
        rotateAnimation.setInterpolator(new LinearInterpolator());
        ivAnim1.setAnimation(rotateAnimation);
        rotateAnimation.startNow();

        // scale animation of ivAnim2 and ivAnim3
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 2.6f, 1f, 2.6f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(2000);
        scaleAnimation.setRepeatCount(ScaleAnimation.INFINITE);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        ivAnim2.setAnimation(scaleAnimation);
        scaleAnimation.startNow();


        findViewById(R.id.iv_anim_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api.login("a070a7d9", new JsonApiBase.OnJsonResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Result result = Result.parse(response);
                        if (result.isSuccess()) {
                            final SignInBean bean = SignInBean.objectFromData(result.mData + "");
                            if (bean == null) {
                                onError("登录异常，请重试");
                            } else {
                                dismissProgressDialog();
                                showShortToast("登录成功");
                                App.getApp().setSignInBean(result.mData + "", bean);
                                App.getApp().mToken = bean.token;
                                ActFrame.startActivity(ActSignInOld.this);
                                finish();
                            }
                        } else {
                            onError(result.mMsg + result.mCode);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        dismissProgressDialog();
                        showTipDialog(error + "");
                        mSignIning = false;
                    }

                    @Override
                    public void onFinish(boolean withoutException) {
                    }
                });
            }
        });
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ActSignInOld.class));
    }
}
