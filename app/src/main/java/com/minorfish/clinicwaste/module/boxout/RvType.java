package com.minorfish.clinicwaste.module.boxout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.clinicwaste.R;
import com.tangjd.common.widget.RvBase;

/**
 * Author: tangjd
 * Date: 2017/6/13
 */

public class RvType extends RvBase<WasteTypeBean> {
    public RvType(Context context) {
        super(context);
    }

    public RvType(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvType(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new GridLayoutManager(context,4);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.rv_out_type_item_layout;
    }

    @Override
    public void customConvert(BaseViewHolder holder, WasteTypeBean bean) {
        holder.setText(R.id.tv_type, bean.mName);
        if(bean.check){
            holder.setImageResource(R.id.iv_choose, R.drawable.ic_check_yes);
        }else{
            holder.setImageResource(R.id.iv_choose, R.drawable.ic_check_no);
        }
    }
}
