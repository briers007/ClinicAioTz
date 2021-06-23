package com.minorfish.clinicwaste.module.boxout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.clinicwaste.R;
import com.tangjd.common.widget.RvBase;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Author: tangjd
 * Date: 2017/6/13
 */

public class RvBags extends RvBase<BagBean> {
    public RvBags(Context context) {
        super(context);
    }

    public RvBags(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvBags(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.rv_bags_item_layout;
    }

    @Override
    public void customConvert(BaseViewHolder holder, BagBean bean) {
        holder.setText(R.id.tv_time, bean.staffTime)
                .setText(R.id.tv_agency, bean.wardName)
                .setText(R.id.tv_type, bean.trashTypeName)
                .setText(R.id.tv_weight, new BigDecimal(bean.weight).setScale(2, RoundingMode.HALF_UP).floatValue() + "Kg");
    }
}
