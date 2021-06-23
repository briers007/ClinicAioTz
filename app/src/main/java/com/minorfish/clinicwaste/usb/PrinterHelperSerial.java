package com.minorfish.clinicwaste.usb;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;

import com.dp.dp_serialportlist.Serialport_Factory;
import com.szzk.serialport.SzzkSerialPort;
import com.tangjd.common.utils.Log;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Author: Administrator
 * Date: 2018/4/11
 */

public class PrinterHelperSerial {

    private final int CONNECTRESULT= 001;
    private static PrinterHelperSerial sPrinterHelper;
    private Context mContext;

    public String qrcode = "";

    public static Serialport_Factory sf;

    public PrinterHelperSerial(Context context) {
        mContext = context;
        sf=Serialport_Factory.getSerialport_Factory(context);
    }

    public Boolean printBagIn(PrintBean bean) {
        if(sf == null || !sf.isConnection())
        {
            Toast.makeText(mContext,  "未连接，请稍等", LENGTH_LONG).show();
            connect();
            return false;
        }
        //sf.LabelBegin(384, 250);    //小纸尺寸
        sf.LabelBegin(384, 560);    //大纸尺寸
        sf.LableText(120, 40, 2, 0, "医疗废物");
        sf.LableText(60, 115, 1, 0, bean.hospitalName);
        sf.LableText(60, 160, 1, 0, bean.typeName);
        sf.LableText(60, 205, 1, 0, bean.weight);
        sf.LableText(60, 250, 1, 0, bean.time);
        sf.LableText(60, 295, 1, 0, bean.sjr);
        sf.LabelQRCode(110, 330, 4, qrcode);

        sf.Labelend();
        sf.PaperCut();

        return true;
    }

    public Boolean printBagInView(View view, Bitmap bitmap) {
        if(sf == null || !sf.isConnection())
        {
            Toast.makeText(mContext,  "未连接，请稍等", LENGTH_LONG).show();
            connect();
            return false;
        }
        sf.LabelBegin(384, 560);    //大纸尺寸
//        sf.LableViewImage(13, 40, 1, view);
        sf.LableImage(13, 40, 3, bitmap);

        sf.Labelend();
        sf.PaperCut();

        return true;
    }

    public static PrinterHelperSerial getInstance(Context context) {
        if (sPrinterHelper == null) {
            sPrinterHelper = new PrinterHelperSerial(context);
        }
        return sPrinterHelper;
    }

    public void connect(){
        boolean isopen=false;
        isopen=sf.OpenPort("ttyS1", "115200");
        if(isopen)
        {
            Log.e("PrinterHelperSerial", "串口打开成功 ");
        }else {
            Log.e("PrinterHelperSerial", "串口打开失败 ");
        }
    }

}