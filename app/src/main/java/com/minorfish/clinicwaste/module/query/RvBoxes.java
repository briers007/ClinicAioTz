package com.minorfish.clinicwaste.module.query;

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

public class RvBoxes extends RvBase<OutedBoxBean> {
    public RvBoxes(Context context) {
        super(context);
    }

    public RvBoxes(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvBoxes(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.rv_boxes_item_layout;
    }

    @Override
    public void customConvert(BaseViewHolder holder, OutedBoxBean bean) {
        holder.setText(R.id.tv_time, "类型："+ bean.typeName + "")
                .setText(R.id.tv_agency, bean.receiveCompanyName + "")
                .setText(R.id.tv_bags, bean.qty + "袋")
                .setText(R.id.tv_weight, bean.weight + "kg");
    }
}
