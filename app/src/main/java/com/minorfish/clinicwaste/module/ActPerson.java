package com.minorfish.clinicwaste.module;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.App;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tangjd on 2016/8/10.
 */
public class ActPerson extends BaseActivity {
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_person_layout);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        /*toolbarTitle.setTextSize(30);
        setToolbarTitle("个人中心");
        enableBackFinish();*/

        tvTitle.setText("个人中心");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (App.getApp().mUserBean != null) {
            ((TextView) findViewById(R.id.agency_name)).setText(App.getApp().mUserBean.instName + "");
            ((TextView) findViewById(R.id.agency_address)).setText(App.getApp().mUserBean.instAddress + "");
            ((TextView) findViewById(R.id.operator_person)).setText(App.getApp().mUserBean.name + "");
        }
        findViewById(R.id.btn_modify_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActModifyPwd.start(ActPerson.this);
            }
        });
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ActPerson.class));
    }
}
