package com.minorfish.clinicwaste;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tangjd.common.R.drawable;
import com.tangjd.common.R.id;
import com.tangjd.common.abs.GlideApp;
import com.tangjd.common.abs.GlideRequest;
import com.umeng.analytics.MobclickAgent;
import com.werb.permissionschecker.PermissionChecker;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public class BaseActivity<T> extends AppCompatActivity {
    private Toolbar mToolbar;
    private Toast mToast;
    private Snackbar mSnackbar;
    public View mContentView;
    public ProgressDialog mProgressDialog;
    public static String EXTRA_COMMON_DATA_BEAN = "extra_data_bean";
    public static final int REQUEST_CODE_COMMON = 9999;
    public Serializable mCommonBean;
    public PermissionChecker mPermissionChecker;
    BaseActivity.PermissionRequestCallback mPermissionCallback;

    public BaseActivity() {
    }

    public void setStatusBarColor(int color) {
        if (VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.clearFlags(201326592);
            window.addFlags(-2147483648);
            window.setStatusBarColor(color);
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            this.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public Toolbar getToolbar() {
        if (this.mToolbar == null) {
            this.mToolbar = (Toolbar)this.findViewById(id.toolbar);
            this.setSupportActionBar(this.mToolbar);
            this.getSupportActionBar().setTitle("");
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        return this.mToolbar;
    }

    public void enableMenu(Menu menu, String menuTitle, OnMenuItemClickListener listener) {
        this.enableMenu(menu, new String[]{menuTitle}, new OnMenuItemClickListener[]{listener});
    }

    public void enableMenu(Menu menu, String[] menuTitles, OnMenuItemClickListener[] listeners) {
        this.getToolbar();

        for(int i = 0; i < menuTitles.length; ++i) {
            MenuItem menuItem = menu.add(menuTitles[i]);
            menuItem.setShowAsAction(6);
            menuItem.setOnMenuItemClickListener(listeners[i]);
        }

    }

    public void enableMenu(Menu menu, int menuIconResId, OnMenuItemClickListener listener) {
        this.enableMenu(menu, new int[]{menuIconResId}, new OnMenuItemClickListener[]{listener});
    }

    public void enableMenu(Menu menu, int[] menuIconResIds, OnMenuItemClickListener[] listeners) {
        this.getToolbar();

        for(int i = 0; i < menuIconResIds.length; ++i) {
            MenuItem menuItem = menu.add("");
            menuItem.setIcon(menuIconResIds[i]);
            menuItem.setShowAsAction(2);
            menuItem.setOnMenuItemClickListener(listeners[i]);
        }

    }

    public void enableMenu(Menu menu, String[] menuTitles, int[] menuIconResIds, OnMenuItemClickListener[] listeners) {
        this.getToolbar();

        for(int i = 0; i < menuIconResIds.length; ++i) {
            MenuItem menuItem = menu.add(menuTitles[i]);
            menuItem.setIcon(menuIconResIds[i]);
            menuItem.setShowAsAction(2);
            menuItem.setOnMenuItemClickListener(listeners[i]);
        }

    }

    public void enableCollapseMenu(Menu menu, String[] menuTitles, OnMenuItemClickListener[] listeners) {
        this.getToolbar();

        for(int i = 0; i < menuTitles.length; ++i) {
            MenuItem menuItem = menu.add(menuTitles[i]);
            menuItem.setShowAsAction(8);
            menuItem.setOnMenuItemClickListener(listeners[i]);
        }

    }

    public void enableLeftMenu(String menuTitle, OnClickListener listener) {
        this.enableLeftMenu(menuTitle, 0, listener);
    }

    public void enableLeftMenu(String menuTitle, int resId, OnClickListener listener) {
        TextView tvLeftMenu = (TextView)this.findViewById(id.toolbar_left_menu);
        tvLeftMenu.setText(menuTitle);
        tvLeftMenu.setVisibility(View.VISIBLE);
        tvLeftMenu.setOnClickListener(listener);
        if (resId != 0) {
            tvLeftMenu.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
            tvLeftMenu.setCompoundDrawablePadding(10);
        }

    }

    public void setToolbarTitle(String title) {
        this.getToolbar();
        ((TextView)this.findViewById(id.toolbar_title)).setText(title);
    }

    public void enableBackFinish() {
        this.getToolbar();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getToolbar().setNavigationIcon(drawable.ic_menu_back);
    }

    private Toast getToast() {
        if (this.mToast == null) {
            this.mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }

        return this.mToast;
    }

    public void showToast() {
        this.showToast("加载中...");
    }

    public void showToast(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (!BaseActivity.this.isFinishing()) {
                    BaseActivity.this.getToast().setText(message);
                    BaseActivity.this.getToast().setDuration(Toast.LENGTH_SHORT);
                    BaseActivity.this.getToast().show();
                }

            }
        });
    }

    public void dismissToast() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (BaseActivity.this.mToast != null) {
                    BaseActivity.this.mToast.cancel();
                }

            }
        });
    }

    public void showShortToast(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (!BaseActivity.this.isFinishing()) {
                    BaseActivity.this.getToast().setText(message);
                    BaseActivity.this.getToast().setDuration(Toast.LENGTH_SHORT);
                    BaseActivity.this.getToast().show();
                }

            }
        });
    }

    public void showLongToast(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (!BaseActivity.this.isFinishing()) {
                    BaseActivity.this.getToast().setText(message);
                    BaseActivity.this.getToast().setDuration(Toast.LENGTH_SHORT);
                    BaseActivity.this.getToast().show();
                }

            }
        });
    }

    public void showTipDialog(String message) {
        this.showTipDialog(message, true, (android.content.DialogInterface.OnClickListener)null);
    }

    public void showTipDialog(String message, android.content.DialogInterface.OnClickListener onPositiveClick) {
        this.showTipDialog(message, false, onPositiveClick);
    }

    public void showTipDialog(String message, boolean cancelable, android.content.DialogInterface.OnClickListener onPositiveClick) {
        this.showTipDialog(message, cancelable, this.getString(R.string.tip_msg), onPositiveClick);
    }

    public void showTipDialog(String message, boolean cancelable, String positiveText, android.content.DialogInterface.OnClickListener onPositiveClick) {
        try {
            Builder builder = new Builder(this);
            builder.setMessage(message);
            builder.setPositiveButton(positiveText, onPositiveClick);
            builder.setCancelable(cancelable);
            if (!this.isFinishing()) {
                builder.create().show();
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    public void showAlertDialog(String message, android.content.DialogInterface.OnClickListener onPositiveClick, android.content.DialogInterface.OnClickListener onNegativeClick) {
        this.showAlertDialog(message, this.getString(R.string.tip_msg), true, onPositiveClick, onNegativeClick);
    }

    public void showAlertDialog(String message, boolean cancelable, android.content.DialogInterface.OnClickListener onPositiveClick, android.content.DialogInterface.OnClickListener onNegativeClick) {
        this.showAlertDialog(message, this.getString(R.string.tip_msg), cancelable, onPositiveClick, onNegativeClick);
    }

    public void showAlertDialog(String message, String positiveText, android.content.DialogInterface.OnClickListener onPositiveClick, android.content.DialogInterface.OnClickListener onNegativeClick) {
        this.showAlertDialog(message, positiveText, true, onPositiveClick, onNegativeClick);
    }

    public void showAlertDialog(String message, String positiveText, boolean cancelable, android.content.DialogInterface.OnClickListener onPositiveClick, android.content.DialogInterface.OnClickListener onNegativeClick) {
        this.showAlertDialog(message, positiveText, this.getString(R.string.tip_msg), cancelable, onPositiveClick, onNegativeClick);
    }

    public void showAlertDialog(String message, String positiveText, String negativeText, boolean cancelable, android.content.DialogInterface.OnClickListener onPositiveClick, android.content.DialogInterface.OnClickListener onNegativeClick) {
        try {
            Builder builder = new Builder(this);
            builder.setMessage(message);
            builder.setCancelable(cancelable);
            builder.setPositiveButton("确定", onPositiveClick);
            builder.setNegativeButton("取消", onNegativeClick);
            if (!this.isFinishing()) {
                builder.create().show();
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }

    }

    public void showSingleChoiceDialog(String[] items, android.content.DialogInterface.OnClickListener listener) {
        try {
            Builder builder = new Builder(this);
            builder.setItems(items, listener);
            if (!this.isFinishing()) {
                builder.create().show();
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void showSingleChoiceDialog(List<T> items, android.content.DialogInterface.OnClickListener listener) {
        try {
            Builder builder = new Builder(this);
            String[] arr = new String[items.size()];

            for(int i = 0; i < items.size(); ++i) {
                arr[i] = items.get(i).toString();
            }

            builder.setItems(arr, listener);
            if (!this.isFinishing()) {
                builder.create().show();
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    public void showMultiChoiceDialog(boolean cancelable, List<T> items, android.content.DialogInterface.OnClickListener positiveClickListener, android.content.DialogInterface.OnClickListener negativeClickListener, OnMultiChoiceClickListener onMultiChoiceClickListener) {
        try {
            Builder builder = new Builder(this);
            String[] arr = new String[items.size()];

            for(int i = 0; i < items.size(); ++i) {
                arr[i] = items.get(i).toString();
            }

            builder.setMultiChoiceItems(arr, (boolean[])null, onMultiChoiceClickListener);
            builder.setPositiveButton("确定", positiveClickListener);
            builder.setNegativeButton("取消", negativeClickListener);
            builder.setCancelable(cancelable);
            if (!this.isFinishing()) {
                builder.create().show();
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }

    }

    public void showMultiChoiceDialog(boolean cancelable, List<T> items, android.content.DialogInterface.OnClickListener positiveClickListener, android.content.DialogInterface.OnClickListener negativeClickListener, android.content.DialogInterface.OnClickListener neutralClickListener,OnMultiChoiceClickListener onMultiChoiceClickListener) {
        try {
            Builder builder = new Builder(this);
            String[] arr = new String[items.size()];

            for(int i = 0; i < items.size(); ++i) {
                arr[i] = items.get(i).toString();
            }

            builder.setMultiChoiceItems(arr, (boolean[])null, onMultiChoiceClickListener);
            builder.setPositiveButton("确定", positiveClickListener);
            builder.setNegativeButton("取消", negativeClickListener);
            builder.setNeutralButton("全选", neutralClickListener);
            builder.setCancelable(cancelable);
            if (!this.isFinishing()) {
                builder.create().show();

                Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                mAlert.setAccessible(true);
                Object mAlertController = mAlert.get(builder);
                Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");

            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }

    }


    public void setContentView(@LayoutRes int layoutResID) {
        this.setContentView(LayoutInflater.from(this).inflate(layoutResID, (ViewGroup)null, false));
    }

    public void setContentView(View view) {
        this.mContentView = view;
        super.setContentView(view);
    }

    public void setContentView(View view, LayoutParams params) {
        this.mContentView = view;
        super.setContentView(view, params);
    }

    private Snackbar getSnackbar() {
        if (this.mSnackbar == null) {
            this.mSnackbar = Snackbar.make(this.mContentView, "", -1);
        }

        return this.mSnackbar;
    }

    public void showShortSnackbar(final String content) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (!BaseActivity.this.isFinishing()) {
                    BaseActivity.this.getSnackbar().setText(content);
                    BaseActivity.this.getSnackbar().setDuration(-1);
                    BaseActivity.this.getSnackbar().show();
                }

            }
        });
    }

    public void showLongSnackbar(final String content) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (!BaseActivity.this.isFinishing()) {
                    BaseActivity.this.getSnackbar().setText(content);
                    BaseActivity.this.getSnackbar().setDuration(0);
                    BaseActivity.this.getSnackbar().show();
                }

            }
        });
    }

    public void showLongSnackbarWithAction(final String content, final String actionText, final OnClickListener listener) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (!BaseActivity.this.isFinishing()) {
                    BaseActivity.this.getSnackbar().setText(content);
                    BaseActivity.this.getSnackbar().setDuration(0);
                    BaseActivity.this.getSnackbar().setAction(actionText, listener);
                    BaseActivity.this.getSnackbar().show();
                }

            }
        });
    }

    public void showSnackbarWithActionIndefinite(final String content, final String actionText, final OnClickListener listener) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (!BaseActivity.this.isFinishing()) {
                    BaseActivity.this.getSnackbar().setText(content);
                    BaseActivity.this.getSnackbar().setDuration(-2);
                    BaseActivity.this.getSnackbar().setAction(actionText, listener);
                    BaseActivity.this.getSnackbar().show();
                }

            }
        });
    }

    public void dismissSnackbar() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (!BaseActivity.this.isFinishing() && BaseActivity.this.mSnackbar != null && BaseActivity.this.mSnackbar.isShown()) {
                    BaseActivity.this.mSnackbar.dismiss();
                }

            }
        });
    }

    public void showProgressDialog(String title, CharSequence message, boolean cancelable) {
        if (this.mProgressDialog == null) {
            this.mProgressDialog = new ProgressDialog(this);
        }

        this.mProgressDialog.setTitle(title);
        this.mProgressDialog.setCancelable(cancelable);
        this.mProgressDialog.setMessage(message);
        if (!this.isFinishing()) {
            try {
                this.mProgressDialog.show();
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

    }

    public void showProgressDialog() {
        this.showProgressDialog("", "加载中...", true);
    }

    public void showProgressDialog(boolean cancelable) {
        this.showProgressDialog("", "加载中...", cancelable);
    }

    public void showProgressDialog(CharSequence message) {
        this.showProgressDialog("", message, true);
    }

    public void dismissProgressDialog() {
        try {
            if (this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
                this.mProgressDialog.cancel();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public RequestOptions getDefaultRequestOption() {
        return RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL);
    }

    public void loadImage(Object urlOrFileOrPath, ImageView imageView) {
        this.loadImage(urlOrFileOrPath, imageView, drawable.ic_default);
    }

    public void loadImage(Object urlOrFileOrPath, ImageView imageView, int loadingRes) {
        GlideApp.with(this).load(urlOrFileOrPath).placeholder(loadingRes).error(loadingRes).fallback(loadingRes).apply(this.getDefaultRequestOption()).into(imageView);
    }

    public void loadImageCenterCrop(Object urlOrFileOrPath, ImageView imageView) {
        this.loadImageCenterCrop(urlOrFileOrPath, imageView, drawable.ic_default);
    }

    public void loadImageCenterCrop(Object urlOrFileOrPath, ImageView imageView, int loadingRes) {
        GlideRequest var10000 = GlideApp.with(this).load(urlOrFileOrPath).placeholder(loadingRes).error(loadingRes).fallback(loadingRes);
        this.getDefaultRequestOption();
        var10000.apply(RequestOptions.centerCropTransform()).into(imageView);
    }

    public void loadImageFitCenter(Object urlOrFileOrPath, ImageView imageView) {
        this.loadImageFitCenter(urlOrFileOrPath, imageView, drawable.ic_default);
    }

    public void loadImageFitCenter(Object urlOrFileOrPath, ImageView imageView, int loadingRes) {
        GlideRequest var10000 = GlideApp.with(this).load(urlOrFileOrPath).placeholder(loadingRes).error(loadingRes).fallback(loadingRes);
        this.getDefaultRequestOption();
        var10000.apply(RequestOptions.fitCenterTransform()).into(imageView);
    }

    public void loadImageCenterInside(Object urlOrFileOrPath, ImageView imageView) {
        this.loadImageCenterInside(urlOrFileOrPath, imageView, drawable.ic_default);
    }

    public void loadImageCenterInside(Object urlOrFileOrPath, ImageView imageView, int loadingRes) {
        GlideRequest var10000 = GlideApp.with(this).load(urlOrFileOrPath).placeholder(loadingRes).error(loadingRes).fallback(loadingRes);
        this.getDefaultRequestOption();
        var10000.apply(RequestOptions.centerInsideTransform()).into(imageView);
    }

    public void loadImageRound(Object urlOrFileOrPath, ImageView imageView) {
        this.loadImageRound(urlOrFileOrPath, imageView, drawable.ic_default);
    }

    public void loadImageRound(Object urlOrFileOrPath, ImageView imageView, int loadingRes) {
        GlideRequest var10000 = GlideApp.with(this).load(urlOrFileOrPath).placeholder(loadingRes).error(loadingRes).fallback(loadingRes);
        this.getDefaultRequestOption();
        var10000.apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        this.mCommonBean = this.getIntent().getSerializableExtra(EXTRA_COMMON_DATA_BEAN);
    }

    public void startAct(Class<?> cls) {
        this.startActivity(new Intent(this, cls));
    }

    public void startAct(Class<?> cls, Serializable bean) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(EXTRA_COMMON_DATA_BEAN, bean);
        this.startActivity(intent);
    }

    public void startAct(Class<?> cls, Serializable... bean) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(EXTRA_COMMON_DATA_BEAN, bean);
        this.startActivity(intent);
    }

    public void startActForResult(Class<?> cls) {
        this.startActivityForResult(new Intent(this, cls), 9999);
    }

    public void startActForResult(Class<?> cls, Serializable bean) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(EXTRA_COMMON_DATA_BEAN, bean);
        this.startActivityForResult(intent, 9999);
    }

    public void startActForResult(Class<?> cls, Serializable... bean) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(EXTRA_COMMON_DATA_BEAN, bean);
        this.startActivityForResult(intent, 9999);
    }

    public void startActForResult(Class<?> cls, int requestCode) {
        this.startActivityForResult(new Intent(this, cls), requestCode);
    }

    public void startActForResult(Class<?> cls, int requestCode, Serializable bean) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(EXTRA_COMMON_DATA_BEAN, bean);
        this.startActivityForResult(intent, requestCode);
    }

    public void startActForResult(Class<?> cls, int requestCode, Serializable... bean) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(EXTRA_COMMON_DATA_BEAN, bean);
        this.startActivityForResult(intent, requestCode);
    }

    public void setResult(int resultCode, Serializable bean) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_COMMON_DATA_BEAN, bean);
        super.setResult(resultCode, intent);
    }

    public void mayRequestPermission(String[] permissionArr, BaseActivity.PermissionRequestCallback callback) {
        if (this.mPermissionChecker == null) {
            this.mPermissionChecker = new PermissionChecker(this);
        }

        this.mPermissionCallback = callback;
        if (this.mPermissionChecker.isLackPermissions(permissionArr)) {
            this.mPermissionChecker.requestPermissions();
        } else {
            callback.onSuccess();
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 0:
                if (this.mPermissionChecker.hasAllPermissionsGranted(grantResults)) {
                    this.mPermissionCallback.onSuccess();
                } else {
                    this.mPermissionChecker.showDialog();
                }
            default:
        }
    }

    public void finish() {
        super.finish();
        this.overridePendingTransition(0, 0);
    }

    public interface PermissionRequestCallback {
        void onSuccess();
    }


    //友盟
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
