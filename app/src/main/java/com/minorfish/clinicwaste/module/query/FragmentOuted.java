package com.minorfish.clinicwaste.module.query;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.Api;
import com.minorfish.clinicwaste.abs.Result;
import com.minorfish.clinicwaste.module.query.boxdetail.ActBoxDetail;
import com.tangjd.common.abs.JsonApiBase;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author: tangjd
 * Date: 2017/6/16
 */

public class FragmentOuted extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener {
    @Bind(R.id.rv_boxes)
    RvBoxes rvBoxes;
    private View mLayoutView;
    private ActQuery mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mLayoutView == null) {
            mLayoutView = inflater.inflate(R.layout.fragment_outed_layout, container, false);
            mActivity = (ActQuery) getActivity();
            ButterKnife.bind(this, mLayoutView);
            rvBoxes.setOnEmptyViewClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getData();
                }
            });
            rvBoxes.addOnItemTouchListener(new OnItemClickListener() {
                @Override
                public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ActBoxDetail.startActivity(mActivity, (OutedBoxBean)adapter.getItem(position));
                }
            });
            getData();
        } else {
            ViewGroup parent = (ViewGroup) mLayoutView.getParent();
            if (parent != null) {
                parent.removeView(mLayoutView);
            }
        }
        ButterKnife.bind(this, mLayoutView);
        return mLayoutView;
    }

    private int mCurrPage;

    private void getData() {
        rvBoxes.showLoadingView();
        mCurrPage = 1;
        Api.queryOutedInfoV2(mCurrPage, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    boolean hasMore = ((JSONObject) result.mData).optBoolean("hasNextPage");
                    rvBoxes.onGetDataSuccess(hasMore, OutedBoxBean.arrayOutedBoxBeanFromData((((JSONObject) result.mData).optJSONArray("list")).toString()), FragmentOuted.this);
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String error) {
                rvBoxes.onGetDataFail(error, true);
            }

            @Override
            public void onFinish(boolean withoutException) {
            }
        });

    }

    @Override
    public void onLoadMoreRequested() {
        Api.queryOutedInfoV2(++mCurrPage, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    boolean hasMore = ((JSONObject) result.mData).optBoolean("hasNextPage");
                    rvBoxes.onLoadMoreSuccess(hasMore, OutedBoxBean.arrayOutedBoxBeanFromData((((JSONObject) result.mData).optJSONArray("list")).toString()));
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String error) {
                rvBoxes.onLoadMoreFail();
            }

            @Override
            public void onFinish(boolean withoutException) {

            }
        });
    }
}
