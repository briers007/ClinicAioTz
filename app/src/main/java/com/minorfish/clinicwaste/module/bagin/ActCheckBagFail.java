package com.minorfish.clinicwaste.module.bagin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.Api;
import com.minorfish.clinicwaste.abs.Constants;
import com.minorfish.clinicwaste.abs.Result;
import com.minorfish.clinicwaste.module.BagInBean;
import com.minorfish.clinicwaste.module.bagin.weight.ActCheckWeight;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Author: tangjd
 * Date: 2017/6/17
 */

public class ActCheckBagFail extends BaseActivity {
    @Bind(R.id.tv_agency_name)
    TextView tvAgencyName;
    @Bind(R.id.tv_weight_time)
    TextView tvWeightTime;
    @Bind(R.id.tv_batch_num)
    TextView tvBatchNum;
    @Bind(R.id.tv_trah_type)
    TextView tvTrahType;
    @Bind(R.id.reason)
    EditText etReason;
    //    @Bind(R.id.rv_photo_multi_choose)
//    RvPhotoMultiChoose rvPhotoMultiChoose;
    @Bind(R.id.btn_commit)
    TextView btnCommit;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar_left_menu)
    TextView toolbarLeftMenu;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    private BagInBean mBagInBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_check_bag_fail_layout);
        ButterKnife.bind(this);
        /*toolbarTitle.setTextSize(30);
        setToolbarTitle("异常处理");
        enableBackFinish();*/

        tvTitle.setText("异常处理");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBagInBean = (BagInBean) getIntent().getSerializableExtra(Constants.EXTRA_BAG_IN_BEAN);

        setData((CheckBagBean) getIntent().getSerializableExtra(Constants.EXTRA_CHECK_BAG_BEAN));
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etReason.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    showTipDialog("请填写异常原因");
                    return;
                }
                mBagInBean.reason = text;
                mBagInBean.status = 1;
                ActCheckWeight.start(ActCheckBagFail.this, mBagInBean, Constants.REQUEST_CODE_ADD_BAG);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //rvPhotoMultiChoose.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_ADD_BAG && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void uploadPhotos(List<String> photos) {
        showProgressDialog();
        Api.uploadPhoto(photos, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                Log.e("TTT", e.getMessage() + "\nid=" + id);
                dismissProgressDialog();
                showTipDialog("上传失败，请重试");
            }

            @Override
            public void onResponse(String response, int id) {
                dismissProgressDialog();
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    JSONArray array = (JSONArray) result.mData;
                    mBagInBean.images = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.optJSONObject(i);
                        boolean itemSuccess = item.optBoolean("success");
                        if (!itemSuccess) {
                            showTipDialog("上传失败，请重试");
                            return;
                        }
                        mBagInBean.images.add(item.optString("url"));
                    }
                    ActCheckWeight.start(ActCheckBagFail.this, mBagInBean, Constants.REQUEST_CODE_ADD_BAG);
                } else {
                    showTipDialog(result.mMsg);
                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                Log.e("TTT", progress + "    " + total + "    " + id);
                showProgressDialog("上传中……" + ((int) (progress * 100f)) + "%");
                if (progress == 1) {
                    showProgressDialog();
                }
            }
        });
    }

    private void setData(CheckBagBean bean) {
        if (bean != null) {
            tvAgencyName.setText(bean.clinicName + "");
            tvBatchNum.setText(bean.batchNo);
            tvTrahType.setText(bean.type + "");
            tvWeightTime.setText(bean.outTime);
        }
    }

    public static void startActivity(Context context, BagInBean bean, CheckBagBean checkBagBean) {
        Intent intent = new Intent(context, ActCheckBagFail.class);
        intent.putExtra(Constants.EXTRA_BAG_IN_BEAN, bean);
        intent.putExtra(Constants.EXTRA_CHECK_BAG_BEAN, checkBagBean);
        context.startActivity(intent);
    }

    public static void start(ActCheckBag context, BagInBean bean, CheckBagBean checkBagBean, int requestCode) {
        Intent intent = new Intent(context, ActCheckBagFail.class);
        intent.putExtra(Constants.EXTRA_BAG_IN_BEAN, bean);
        intent.putExtra(Constants.EXTRA_CHECK_BAG_BEAN, checkBagBean);
        context.startActivityForResult(intent, requestCode);
    }
}
