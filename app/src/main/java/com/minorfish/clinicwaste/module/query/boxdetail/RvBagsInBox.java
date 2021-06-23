package com.minorfish.clinicwaste.module.query.boxdetail;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.clinicwaste.R;
import com.tangjd.common.widget.RvBase;

/**
 * Author: tangjd
 * Date: 2017/6/13
 */

public class RvBagsInBox extends RvBase<BagInBoxBean> {
    public RvBagsInBox(Context context) {
        super(context);
    }

    public RvBagsInBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvBagsInBox(Context context, @Nullable AttributeSet attrs, int defStyle) {
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
    public void customConvert(BaseViewHolder holder, BagInBoxBean bean) {
        holder.setText(R.id.tv_time, bean.time)
                .setText(R.id.tv_agency, bean.clinicName)
                .setText(R.id.tv_type, bean.type)
                .setText(R.id.tv_weight, bean.weight + "Kg");
    }
}
