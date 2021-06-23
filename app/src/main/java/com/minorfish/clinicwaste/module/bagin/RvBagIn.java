package com.minorfish.clinicwaste.module.bagin;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.module.BagInBean;
import com.tangjd.common.widget.RvBase;

/**
 * Author: tangjd
 * Date: 2017/6/20
 */

public class RvBagIn extends RvBase<BagInBean> {
    public RvBagIn(Context context) {
        super(context);
    }

    public RvBagIn(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvBagIn(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.rv_bag_in_item_layout;
    }

    @Override
    public void customConvert(BaseViewHolder holder, BagInBean bean) {
        holder.setText(R.id.tv_name, bean.clinicName + "")
                .setText(R.id.tv_type, "垃圾类型：" + bean.wasteType)
                .setText(R.id.tv_weight, bean.weight + "");
    }
}
