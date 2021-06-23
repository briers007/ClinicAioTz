package com.minorfish.clinicwaste.module;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.Api;
import com.minorfish.clinicwaste.abs.App;
import com.minorfish.clinicwaste.abs.Result;
import com.minorfish.clinicwaste.module.signin.ActSignInPwd;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.StringUtil;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author: tangjd
 * Date: 2017/7/11
 */

public class ActModifyPwd extends BaseActivity {
    @Bind(R.id.et_old_pwd)
    EditText etOldPwd;
    @Bind(R.id.et_new_pwd)
    EditText etNewPwd;
    @Bind(R.id.et_confirm_pwd)
    EditText etConfirmPwd;
    @Bind(R.id.btn_confirm)
    TextView btnConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_modify_pwd_layout);
        ButterKnife.bind(this);
        setToolbarTitle("修改密码");
        enableBackFinish();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(etOldPwd)) {
                    showShortToast("请填写旧密码");
                    return;
                }
                if (StringUtil.isEmpty(etNewPwd)) {
                    showShortToast("请填写新密码");
                    return;
                }
                if (StringUtil.isEmpty(etConfirmPwd)) {
                    showShortToast("请再次填写新密码");
                    return;
                }
                if (!etNewPwd.getText().toString().equals(etConfirmPwd.getText().toString())) {
                    showShortToast("两次密码输入不一致");
                    return;
                }
                showProgressDialog();
                Api.modifyPwd(etOldPwd.getText().toString(), etNewPwd.getText().toString(), new JsonApiBase.OnJsonResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Result result = Result.parse(response);
                        if (result.isSuccess()) {
                            showLongToast("修改密码成功，请使用新密码登录");
                            App.getApp().signOut();
                            ActSignInPwd.startActivity(ActModifyPwd.this);
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

    public static void start(Context context) {
        context.startActivity(new Intent(context, ActModifyPwd.class));
    }
}
