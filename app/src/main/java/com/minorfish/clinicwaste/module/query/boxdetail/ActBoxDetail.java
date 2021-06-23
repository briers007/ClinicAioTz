package com.minorfish.clinicwaste.module.query.boxdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.Api;
import com.minorfish.clinicwaste.abs.Constants;
import com.minorfish.clinicwaste.abs.Result;
import com.minorfish.clinicwaste.module.query.OutedBoxBean;
import com.tangjd.common.abs.JsonApiBase;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author: tangjd
 * Date: 2017/6/16
 */

public class ActBoxDetail extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener {
    @Bind(R.id.rv_bags_in_box)
    RvBagsInBox rvBagsInBox;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar_left_menu)
    TextView toolbarLeftMenu;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private OutedBoxBean mOutedBoxBean;
    private int mCurrPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_box_detail_layout);
        ButterKnife.bind(this);

        toolbarTitle.setTextSize(30);
        setToolbarTitle("垃圾箱详情");
        enableBackFinish();
        mOutedBoxBean = (OutedBoxBean) getIntent().getSerializableExtra(Constants.EXTRA_OUTED_BOX_BEAN);
        getData();
    }

    private void getData() {
        rvBagsInBox.showLoadingView();
        mCurrPage = 1;
        Api.getBoxDetail(mCurrPage, mOutedBoxBean.id, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    boolean hasMore = ((JSONObject) result.mData).optBoolean("hasNextPage");
                    JSONArray data = ((JSONObject) result.mData).optJSONArray("list");
                    rvBagsInBox.onGetDataSuccess(hasMore, BagInBoxBean.arrayBagInBoxBeanFromData(data.toString()), ActBoxDetail.this);
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String error) {
                rvBagsInBox.onGetDataFail(error, true);
            }

            @Override
            public void onFinish(boolean withoutException) {

            }
        });
    }

    public static void startActivity(Context context, OutedBoxBean bean) {
        Intent intent = new Intent(context, ActBoxDetail.class);
        intent.putExtra(Constants.EXTRA_OUTED_BOX_BEAN, bean);
        context.startActivity(intent);
    }

    @Override
    public void onLoadMoreRequested() {
        Api.getBoxDetail(++mCurrPage, mOutedBoxBean.id, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    boolean hasMore = ((JSONObject) result.mData).optBoolean("hasNextPage");
                    JSONArray data = ((JSONObject) result.mData).optJSONArray("list");
                    rvBagsInBox.onLoadMoreSuccess(hasMore, BagInBoxBean.arrayBagInBoxBeanFromData(data.toString()));
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String error) {
                rvBagsInBox.onLoadMoreFail();
            }

            @Override
            public void onFinish(boolean withoutException) {

            }
        });
    }
}
