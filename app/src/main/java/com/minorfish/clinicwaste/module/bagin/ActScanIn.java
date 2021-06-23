package com.minorfish.clinicwaste.module.bagin;

/**
 * Created by tangjd on 2016/8/11.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.Api;
import com.minorfish.clinicwaste.abs.App;
import com.minorfish.clinicwaste.abs.Constants;
import com.minorfish.clinicwaste.abs.Result;
import com.minorfish.clinicwaste.usb.PrinterHelperSerial;
import com.minorfish.clinicwaste.util.NetUtil;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.StringUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActScanIn extends BaseActivity {
    private static final String TAG = ActScanIn.class.getSimpleName();
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar_left_menu)
    TextView toolbarLeftMenu;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_scan)
    EditText etScan;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x001:
                    String codeStr = (String) msg.obj;
                    Log.i(TAG, "handleMessage: " + codeStr);
                    ActCheckBag.start(ActScanIn.this, codeStr, Constants.REQUEST_CODE_ADD_BAG);
                    break;
                case 0x002:
                    dismissProgressDialog();
                    String errorStr = (String) msg.obj;
                    Toast.makeText(ActScanIn.this, "" + errorStr, Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_scan_layout);
        ButterKnife.bind(this);


        /*toolbarTitle.setTextSize(30);
        setToolbarTitle("垃圾袋入库");
        enableBackFinish();*/

        tvTitle.setText("垃圾袋入库");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etScan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
//                Toast.makeText(ActScanIn.this, "afterTextChanged---获取扫描结果："+content, Toast.LENGTH_SHORT).show();
                if (!StringUtil.isEmpty(content) && content.endsWith("\n")) {
                    String code = "";
                    PrinterHelperSerial.getInstance(ActScanIn.this).qrcode = content;
                    content = content.substring(0, content.length() - 1);
                    code = content.substring(content.lastIndexOf("=") + 1);
                    Log.i(TAG, "afterTextChanged: " + content + "---code---" + code);
//                    Toast.makeText(ActScanIn.this, "afterTextChanged---获取扫描结果2--"+code, Toast.LENGTH_SHORT).show();
                    //校验溯源码是否可以使用
                    ActCheckBag.start(ActScanIn.this, code, Constants.REQUEST_CODE_ADD_BAG);
//                    checkSourceCode(code);
                    etScan.setText("");
                }
            }
        });
    }

    private void checkSourceCode(final String mCode) {
        Log.i(TAG, "checkSourceCode: " + mCode);
        if (NetUtil.isNetworkAvailable(this)) {
            showProgressDialog();
            String token = App.getApp().getSignInBean().token;
            String url = Api.getDomainName() + "/hw/clinic/pda/sourceCode/validation";
            Log.i(TAG, "checkSourceCode: " + "token: " + token + "---url: " + url);

            Map<String, String> map = new HashMap<String, String>();
            map.put("sourceCode", mCode);
            Api.getSourceCode(map, new JsonApiBase.OnJsonResponseListener() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Result result = Result.parse(jsonObject);
                    JSONObject resp = (JSONObject) result.mData;
                    boolean validation = resp.optBoolean("validation");
                    if (validation) {
                        Message message = Message.obtain();
                        message.what = 0x001;
                        message.obj = mCode;
                        mHandler.sendMessage(message);
                    } else {
                        String faild = resp.optString("message");
                        Message message = Message.obtain();
                        message.what = 0x002;
                        message.obj = faild;
                        mHandler.sendMessage(message);
                    }
                }

                @Override
                public void onError(String s) {
                    Message message = Message.obtain();
                    message.what = 0x002;
                    message.obj = s;
                    mHandler.sendMessage(message);
                }

                @Override
                public void onFinish(boolean b) {
//                    dismissProgressDialog();
                    Log.i(TAG, "onFinish: " + b);
                    mHandler.sendEmptyMessage(0x003);
                }
            });
        } else {
            Toast.makeText(this, "网络状态异常，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return disableKeycode(keyCode, event);
    }

    private boolean disableKeycode(int keyCode, KeyEvent event) {
        int key = event.getKeyCode();
        switch (key) {
            case KeyEvent.KEYCODE_TAB:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_ADD_BAG && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ActScanIn.class);
        context.startActivity(intent);
    }

    public static void start(BaseActivity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, ActScanIn.class), requestCode);
    }
}