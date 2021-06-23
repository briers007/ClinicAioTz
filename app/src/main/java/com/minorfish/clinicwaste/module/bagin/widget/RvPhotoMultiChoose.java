package com.minorfish.clinicwaste.module.bagin.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;


import com.minorfish.clinicwaste.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * Author: tangjd
 * Date: 2017/6/19
 */

public class RvPhotoMultiChoose extends RecyclerView {
    private PhotoAdapter mAdapter;
    private BaseActivity mActivity;
    public ArrayList<String> selectedPhotos = new ArrayList<>();

    public RvPhotoMultiChoose(Context context) {
        this(context, null);
    }

    public RvPhotoMultiChoose(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RvPhotoMultiChoose(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mActivity = (BaseActivity) context;
        init();
    }

    public int getPhotoCount() {
        return selectedPhotos.size();
    }

    public void setData(ArrayList<String> photos) {
        selectedPhotos = photos;
        mAdapter.notifyDataSetChanged();
    }

    public void addData(String photo) {
        selectedPhotos.add(selectedPhotos.size(), photo);
        mAdapter.notifyItemInserted(selectedPhotos.size());
    }

    private void init() {
        setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));
        mAdapter = new PhotoAdapter(mActivity, selectedPhotos);
        setAdapter(mAdapter);
        addOnItemTouchListener(new RecyclerItemClickListener(mActivity.getApplicationContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (mAdapter.getItemViewType(position) == PhotoAdapter.TYPE_ADD) {
                            PhotoPicker.builder()
                                    .setPhotoCount(PhotoAdapter.MAX)
                                    .setShowCamera(true)
                                    .setPreviewEnabled(true)
                                    .setSelected(selectedPhotos)
                                    .start(mActivity);
                        } else {
                            PhotoPreview.builder()
                                    .setPhotos(selectedPhotos)
                                    .setCurrentItem(position)
                                    .start(mActivity);
                        }
                    }
                }));

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();
            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
