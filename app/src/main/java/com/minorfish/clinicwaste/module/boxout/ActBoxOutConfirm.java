package com.minorfish.clinicwaste.module.boxout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.Api;
import com.minorfish.clinicwaste.abs.Result;
import com.minorfish.clinicwaste.widget.ItemViewSpinner;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.StringKit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author: tangjd
 * Date: 2017/6/13
 */

public class ActBoxOutConfirm extends BaseActivity {
    @Bind(R.id.btn_confirm)
    TextView btnConfirm;
    @Bind(R.id.rv_type)
    RvType rvType;
    @Bind(R.id.item_company)
    ItemViewSpinner itemCompany;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ActBoxOutConfirm.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_out_confirm_layout);
        ButterKnife.bind(this);
        setToolbarTitle("垃圾箱出库");
        enableBackFinish();
        getData();

        rvType.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(typeList != null && typeList.size()!=0) {
                    WasteTypeBean item = typeList.get(position);
                    if(StringKit.isNotEmpty(item.mName)) {
                        if (item.check) {
                            typeList.get(position).check = false;
                        } else {
                            typeList.get(position).check = true;
                        }
                        rvType.setData(typeList);
                    }
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray array = null;
                if(typeList!=null && typeList.size()!=0){
                    array = new JSONArray();
                    for (int i = 0; i < typeList.size(); i++) {
                        if(typeList.get(i).check) {
                            array.put(typeList.get(i).mCode);
                        }
                    }
                }
                if(array==null ||array.length()==0){
                    showLongToast("请选择要出库的类型");
                    return;
                }
                String companyCode = "";
                try {
                    companyCode = companyList.get(itemCompany.spContent.getSelectedIndex()).companyId;
                }catch (Exception e){}
                if(StringKit.isEmpty(companyCode)){
                    showLongToast("请选择回收公司");
                    return;
                }
                confirmOut(companyCode,array);
            }
        });
    }

    private void confirmOut(String companyId,JSONArray array) {
        showProgressDialog();
        Api.boxOut(companyId,array, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    showLongToast("出库成功");
                    setResult(RESULT_OK);
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

    private List<CompanyBean> companyList = new ArrayList<>();
    private List<WasteTypeBean> typeList = new ArrayList<>();

    private void getData() {
        Api.getWasteType(new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    typeList = WasteTypeBean.parse(result.mData);
                    rvType.onGetDataSuccess(typeList);
                } else {
                    onError(result.mMsg + " " + result.mCode);
                }
            }

            @Override
            public void onError(String error) {
            }

            @Override
            public void onFinish(boolean withoutException) {
            }
        });

        Api.getCompanyList(new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess() && result.mData!=null) {
                    companyList = CompanyBean.arrayCompanyBeanFromData(result.mData.toString());
                    if (companyList == null) {
                        companyList = new ArrayList<>();
                    }
                    CompanyBean firstItem = new CompanyBean();
                    firstItem.companyName = "未选择";
                    companyList.add(0, firstItem);
                    itemCompany.spContent.attachDataSource(companyList);
                } else {
                    onError(result.mMsg);
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
}
