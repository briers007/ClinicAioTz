package com.minorfish.clinicwaste.module.frame;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.R;
import com.tangjd.common.utils.DisplayUtils;
import com.tangjd.common.widget.RvBase;

/**
 * Author: tangjd
 * Date: 2017/4/13
 */

public class RvFrame extends RvBase<ResNameBean> {

    private Point mScreenPoint;

    public RvFrame(Context context) {
        super(context);
    }

    public RvFrame(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvFrame(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new GridLayoutManager(context, 3);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.rv_frame_item_layout;
    }

    @Override
    public void customConvert(BaseViewHolder holder, ResNameBean resNameBean) {
        if (mScreenPoint == null) {
            mScreenPoint = DisplayUtils.getScreenSize((BaseActivity) getContext());
        }
        int height = getHeight();
        ViewGroup.LayoutParams params = holder.getConvertView().getLayoutParams();
//        params.height = (mScreenPoint.y - 2 * DisplayUtils.dpToPx(60, getContext().getApplicationContext())) / 2;
        params.height = height / 2;
        holder.setText(R.id.tv_name, resNameBean.mName)
                .setImageResource(R.id.iv_res, resNameBean.mResId);
    }
}
