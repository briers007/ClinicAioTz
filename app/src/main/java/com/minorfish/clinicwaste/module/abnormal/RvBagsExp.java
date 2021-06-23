package com.minorfish.clinicwaste.module.abnormal;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.module.boxout.BagBean;
import com.minorfish.clinicwaste.module.boxout.BagItemBean;
import com.tangjd.common.widget.RvBase;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Author: tangjd
 * Date: 2017/6/13
 */

public class RvBagsExp extends RvBase<BagItemBean> {
    public RvBagsExp(Context context) {
        super(context);
    }

    public RvBagsExp(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvBagsExp(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.adapter_exp_item;
    }

    @Override
    public BaseQuickAdapter<BagItemBean, BaseViewHolder> instanceCustomAdapter() {
//        return super.instanceCustomAdapter();
//        return new RvBase.CustomAdapter(this.customSetItemLayoutId());
        return new RvBase.CustomAdapter(this.customSetItemLayoutId()) {
            @Override
            public void onBindViewHolder(BaseViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                try {
                    int index = position+1;
                    if(index < 10) {
                        holder.setText(R.id.tvIndex, "0" + (position + 1));
                    } else {
                        holder.setText(R.id.tvIndex, "" + (position + 1));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void customConvert(BaseViewHolder holder, BagItemBean bean) {
        holder.setText(R.id.tv_time, "诊所出库时间："+bean.inTime)
                .setText(R.id.tv_no, "编号："+bean.batchNo)
                .setText(R.id.tv_name, "诊所："+bean.clinicName)
                .setText(R.id.tv_waste_type, "类型："+bean.type)
                .setText(R.id.tv_exp_type, "异常类型："+bean.reason)
                .setText(R.id.tv_person_sj, "收集人："+bean.staff)
                .setText(R.id.tv_person_hs, "交接人"+bean.nurse)
                .setText(R.id.tv_weight_2, "诊所收集重量"+bean.inWeight+"Kg")
                .setText(R.id.tv_weight_in, "入库重量"+bean.weight + "Kg");

        holder.setVisible(R.id.tv_person_sj, !TextUtils.isEmpty(bean.staff));
        holder.setVisible(R.id.tv_person_hs, !TextUtils.isEmpty(bean.nurse));
        holder.setVisible(R.id.tv_weight_2, !TextUtils.isEmpty(bean.inWeight));
    }
}
