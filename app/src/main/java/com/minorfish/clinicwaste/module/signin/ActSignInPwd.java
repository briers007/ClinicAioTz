package com.minorfish.clinicwaste.module.signin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.Api;
import com.minorfish.clinicwaste.abs.App;
import com.minorfish.clinicwaste.abs.Result;
import com.minorfish.clinicwaste.module.frame.ActFrame;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.StringUtil;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author: tangjd
 * Date: 2017/6/12
 */

public class ActSignInPwd extends BaseActivity {
    @Bind(R.id.et_account)
    EditText etAccount;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.btn_sign_id)
    Button btnSignId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.act_sign_pwd_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        etAccount.setText("");
        etPwd.setText("");
        if (App.getApp().getSignInBean() != null && !TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
            ActFrame.startActivity(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.getApp().getSignInBean() != null && !TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
            ActFrame.startActivity(this);
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        enableMenu(menu, "工牌登录", new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ActSignIn.startActivity(ActSignInPwd.this);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        setToolbarTitle("");
        findViewById(R.id.toolbar).setBackgroundColor(Color.TRANSPARENT);
        btnSignId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(etAccount)) {
                    showTipDialog("请输入帐号");
                    return;
                }
                if (StringUtil.isEmpty(etPwd)) {
                    showTipDialog("请输入密码");
                    return;
                }
                showProgressDialog();
                Api.login(etAccount.getText().toString(), etPwd.getText().toString(), new JsonApiBase.OnJsonResponseListener() {
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
                            ActFrame.startActivity(ActSignInPwd.this);
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
        });
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ActSignInPwd.class));
    }
}
