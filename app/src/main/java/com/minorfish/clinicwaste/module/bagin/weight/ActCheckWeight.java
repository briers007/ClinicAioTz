package com.minorfish.clinicwaste.module.bagin.weight;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.minorfish.clinicwaste.BaseActivity;
import com.minorfish.clinicwaste.R;
import com.minorfish.clinicwaste.abs.App;
import com.minorfish.clinicwaste.abs.Constants;
import com.minorfish.clinicwaste.aio.serial.SerialPortHelper;
import com.minorfish.clinicwaste.module.BagInBean;
import com.minorfish.clinicwaste.ui.UploadData;
import com.minorfish.clinicwaste.usb.PrintBean;
import com.minorfish.clinicwaste.usb.PrinterHelperSerial;
import com.minorfish.clinicwaste.util.Utils;
import com.tangjd.common.utils.ByteUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tangjd on 2016/8/11.
 */
public class ActCheckWeight extends BaseActivity {
    private static final String TAG = ActCheckWeight.class.getSimpleName();
    @Bind(R.id.btn_upload)
    TextView btnUpload;
    @Bind(R.id.btn_zero)
    TextView btnZero;
    @Bind(R.id.btn_get_weight)
    TextView btnGetWeight;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar_left_menu)
    TextView toolbarLeftMenu;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_view_weight)
    TextView tvViewWeight;
    @Bind(R.id.btn_print)
    TextView btnPrint;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    private float mWeight = 0;


    private BagInBean mBean;

    private boolean mGetWeight = false;
    private float mLastWeight = 0;
    private int sameNume = 0;
    private float zeroWeight = 0;

    TextView tvWeight;


    /**
     * SerialPortHelper????????????????????????????????????????????????byte??????
     */

    private SerialPortHelper serialPortHelper = new SerialPortHelper(new SerialPortHelper.OnGetData() {
        @Override
        public void onDataReceived(byte[] bytes) {
            Log.i(TAG, "serialPortHelper---onDataReceived: " + Arrays.toString(bytes));
            parseData2(bytes);
        }
    });

    //?????????????????????
    private void parseData2(byte[] array) {
        String str2Gson = new Gson().toJson(array);
        Log.i(TAG, "parseData2: " + str2Gson);
        try {
            if (array != null && array.length >= 8 && array[0] == 0x0A && array[1] == 0x0D && (array[2] == 0x2B || array[2] == 0x2D)) {
                String resultStr = ByteUtil.ByteArrayToHexString(array);
                String str = Utils.getChars(array).trim();
                Log.i(TAG, "parseData2--string: " + resultStr + "\n" + "str: " + str);
                int rstInt = Integer.parseInt(str);
                Log.i(TAG, "parseData2--int: " + rstInt);
                float result = ((float) rstInt) / 100f;
                Log.i(TAG, "parseData2: " + result);

                if (array[2] == 0x2D) {
                    Log.i(TAG, "parseData2--result1: " + result);
                    onBtResultGet(-result);
                } else {
                    Log.i(TAG, "parseData2--result2: " + result);
                    onBtResultGet(result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBean = (BagInBean) getIntent().getSerializableExtra(Constants.EXTRA_BAG_IN_BEAN);
        String str = new Gson().toJson(mBean);
        Log.i(TAG, "onCreate: " + str);
        setContentView(R.layout.act_check_weight_layout);
        ButterKnife.bind(this);
        initView();
        serialPortHelper.openSerialPort();//??????????????????
    }

    private void onBtResultGet(final float value) {
       /* if(value <= 0 ) return;
        if(value){}*/
       /* final float weight = Math.round((value - UploadData.getInstance().carWeigth()) * 100) / 100f;
        final float zWeight = Math.round((value - UploadData.getInstance().totalWeight) * 100) / 100f;*/
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvWeight.setText(value + "");
                    mBean.weight = value;
                }
            });

//            onGetWeight(weight+"");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onGetWeight(String weight) {
        UploadData.getInstance().mWaste.mWeight = weight;
        UploadData.getInstance().mWaste.mCreateTime = System.currentTimeMillis();
    }

    private void initView() {
        /*toolbarTitle.setTextSize(30);
        setToolbarTitle("??????");
        enableBackFinish();*/

        tvTitle.setText("??????");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myHandler = new MyHandler(this);
        tvWeight = findViewById(R.id.tv_view_weight);

        UploadData.getInstance().doWeight();

//        ScaleHelper.getInstance().setHandler(myHandler);//??????USB??????

        //??????
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weight = tvWeight.getText().toString().trim();
                if (TextUtils.isEmpty(weight) || weight.equals("0.0") || weight.equals("0")) {
                    showShortToast("????????????");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(Constants.EXTRA_BAG_IN_BEAN, mBean);
                setResult(RESULT_OK, intent);
                finish();
//                goPrint();
            }
        });

        //??????

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPrint();
            }
        });

        //??????
        btnZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ScaleHelper.getInstance().zeroSet();
                serialPortHelper.zeroSet();
            }
        });
    }

    //??????
    protected void setZero() {
        if (mGetWeight) {
            UploadData.getInstance().zeroWeight = zeroWeight;
            showShortToast("????????????");
        }
    }

    private void goPrint() {
        if (mBean != null) {
            mBean.time = System.currentTimeMillis();
            PrintBean printBean = new PrintBean();
            printBean.typeName = "?????????" + mBean.wasteType;
            printBean.weight = "?????????" + mBean.weight + "Kg";
            printBean.time = "?????????" + new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(new Date());
//        printBean.jjr = App.getApp().getSignInBean().principal;
            printBean.sjr = "????????????" + App.getApp().getSignInBean().name;
            printBean.hospitalName = App.getApp().getSignInBean().instName;

            if (PrinterHelperSerial.getInstance(this).printBagIn(printBean)) {
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.EXTRA_BAG_IN_BEAN, mBean);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, 3000);
            }
        } else {
            Toast.makeText(this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGetWeight = false;
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
        }
        if (serialPortHelper != null) {
            serialPortHelper.closeSerialPort();
            serialPortHelper = null;
        }

        UploadData.getInstance().clear();

    }

    private MyHandler myHandler;

    private static class MyHandler extends Handler {
        private final WeakReference<ActCheckWeight> mActivity;

        public MyHandler(ActCheckWeight activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg == null) {
                return;
            }

            //??????USB??????????????????
           /* switch (msg.what) {
                case ScaleHelper.MESSAGE_FROM_SERIAL_PORT:
                    if (mActivity.get() == null) {
                        return;
                    }
                    mActivity.get().array = (byte[]) msg.obj;
                    break;
            }*/
        }
    }

    //????????????
    public static void startActivity(Context context, BagInBean bean) {
        Intent intent = new Intent(context, ActCheckWeight.class);
        intent.putExtra(Constants.EXTRA_BAG_IN_BEAN, bean);
        context.startActivity(intent);
    }

    //?????????????????????
    public static void start(BaseActivity context, BagInBean bean, int requestCodeAddBag) {
        Intent intent = new Intent(context, ActCheckWeight.class);
        intent.putExtra(Constants.EXTRA_BAG_IN_BEAN, bean);
        context.startActivityForResult(intent, requestCodeAddBag);
    }


    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private void starBlueSpeech() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
        try {
            AssetFileDescriptor fileDescriptor = getAssets().openFd("blue.wav");
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            fileDescriptor.close();
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void starDingSpeech() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
        try {
//            AssetFileDescriptor fileDescriptor = getActivity().getAssets().openFd("ding.wav");
            AssetFileDescriptor fileDescriptor = getAssets().openFd("ding.wav");
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            fileDescriptor.close();
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
