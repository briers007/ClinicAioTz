package com.minorfish.clinicwaste.module.bagin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.Api;
import com.minorfish.clinicwaste.abs.App;
import com.minorfish.clinicwaste.abs.Constants;
import com.minorfish.clinicwaste.abs.Result;
import com.minorfish.clinicwaste.module.BagInBean;
import com.minorfish.clinicwaste.module.frame.ActFrame2;
import com.minorfish.clinicwaste.util.NetUtil;
import com.minorfish.clinicwaste.util.OkHttpUtil;
import com.tangjd.common.abs.JsonApiBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 垃圾入库
 */

public class ActBagInList extends BaseActivity {
    private static final String TAG = ActBagInList.class.getSimpleName();
    @Bind(R.id.rv_bag_in)
    RvBagIn rvBagIn;
    @Bind(R.id.btn_add_bag)
    ImageView btnAddBag;
    @Bind(R.id.btn_commit)
    TextView btnCommit;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar_left_menu)
    TextView toolbarLeftMenu;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_add_waste)
    TextView tvAddWaste;

    List<BagInBean> bag2;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x001:
                    dismissProgressDialog();
                    /*String str= (String) msg.obj;
                    Log.i(TAG, "handleMessage: "+str);*/
                    if (bag2 != null) {
                        bag2.clear();
                        bag2 = null;
                    }
                    Toast.makeText(ActBagInList.this, "入库成功", Toast.LENGTH_SHORT).show();
                    ActFrame2.startActivity(ActBagInList.this);
                    break;
                case 0x002:
                    String error = (String) msg.obj;
                    Log.i(TAG, "handleMessage: "+error);
                    Toast.makeText(ActBagInList.this, "入库失败--" + error, Toast.LENGTH_SHORT).show();
                    dismissProgressDialog();
                    break;
            }
        }
    };
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_bag_in_list_layout);
        ButterKnife.bind(this);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bag2!=null && bag2.size()>0){
                    clickExit();
                }else{
                    finish();
                }
            }
        });

        //设置title的背景和大小
        tvTitle.setText("垃圾入库");

        /*toolbarTitle.setTextSize(30);
        setToolbarTitle("垃圾入库");
        enableBackFinish();*/

        bag2 = new ArrayList<BagInBean>();
        //image添加垃圾
        btnAddBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActScanIn.start(ActBagInList.this, Constants.REQUEST_CODE_ADD_BAG);
            }
        });

        //添加垃圾
        tvAddWaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActScanIn.start(ActBagInList.this, Constants.REQUEST_CODE_ADD_BAG);
//                ActScanIn.startActivity(ActBagInList.this);
            }
        });
        //上传垃圾
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BagInBean> beans = rvBagIn.mAdapter.getData();
                if (beans == null || beans.size() == 0) {
                    showTipDialog("没有可上传的垃圾袋");
                    return;
                }
                showProgressDialog(false);

                String url = Api.getDomainName() + "/hw/clinic/pda/bag/in/batch.do";
                String token = App.getApp().mUserBean.token;
                Log.i(TAG, "onClick: "+url+"\n"+token);
                try {
                    JSONObject params = new JSONObject();
                    JSONArray arr = new JSONArray();
                    for (int i = 0; i < beans.size(); i++) {
                        BagInBean bean = beans.get(i);
                        JSONObject obj = new JSONObject();
                        obj.put("bagCode", bean.bagCode);
                        if (!TextUtils.isEmpty(bean.reason)) {
                            obj.put("reason", bean.reason);
                        }
                        obj.put("status", bean.status);
                        obj.put("weight", bean.weight);
                        arr.put(obj);
                    }
                    params.put("data", arr);
                    String jsonStr = params.toString();
                    Log.i(TAG, "onClick: "+jsonStr);

                    OkHttpUtil.getInstance().postJsonAsyn(url, jsonStr, token, new OkHttpUtil.NetCall() {
                        @Override
                        public void success(Call call, Response response) throws IOException {
                            String res = response.body().string();
                            Log.i(TAG, "success: "+res);

                            try {
                                JSONObject jsonObject = new JSONObject(res);
                                int mCode = jsonObject.optInt("code");
                                if(mCode==200){
                                    mHandler.sendEmptyMessage(0x001);
                                }else{
                                    String faild = jsonObject.optString("message");
                                    Message message = Message.obtain();
                                    message.what = 0x002;
                                    message.obj = faild;
                                    mHandler.sendMessage(message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failed(Call call, IOException e) {
                            String faild2 = e.getMessage();
                            Message message = Message.obtain();
                            message.what = 0x002;
                            message.obj = faild2;
                            mHandler.sendMessage(message);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                /*Api.bagIn(rvBagIn.mAdapter.getData(), new JsonApiBase.OnJsonResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Result result = Result.parse(response);
                        String str = new Gson().toJson(response);
                        Log.i(TAG, "onResponse: " + str);
                        if (result.isSuccess()) {
                            mHandler.sendEmptyMessage(0x001);
                        } else {
                            Message message = Message.obtain();
                            message.what = 0x002;
                            message.obj = result.mMsg;
                            mHandler.sendMessage(message);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Message message = Message.obtain();
                        message.what = 0x002;
                        message.obj = error;
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void onFinish(boolean withoutException) {
                        Log.i(TAG, "onFinish: " + withoutException);
                    }
                });*/
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_ADD_BAG && resultCode == RESULT_OK) {
            BagInBean bean = (BagInBean) data.getSerializableExtra(Constants.EXTRA_BAG_IN_BEAN);
            String beanCode = bean.bagCode;
            Log.i(TAG, "onActivityResult: " + beanCode);

            bag2 = rvBagIn.mAdapter.getData();
            String str = new Gson().toJson(bag2);
            Log.i(TAG, "onActivityResult: " + str);

            if (bag2.size() <= 0) {
                rvBagIn.addData(bean);
            } else {
                String ss = new Gson().toJson(bag2);
                Log.i(TAG, "onActivityResult: " + ss);
                if (ss.contains(bean.bagCode)) {
                    Toast.makeText(this, "扎带已使用", Toast.LENGTH_SHORT).show();
                } else {
                    rvBagIn.addData(bean);
                }

            }
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ActBagInList.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            clickExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    private void clickExit() {
        showAlertDialog("是否确认退出？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActFrame2.startActivity(ActBagInList.this);
                bag2.clear();
                finish();
            }
        }, null);
    }


}
