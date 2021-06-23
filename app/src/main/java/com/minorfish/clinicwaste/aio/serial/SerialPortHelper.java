package com.minorfish.clinicwaste.aio.serial;

import com.google.gson.Gson;
import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.tangjd.common.utils.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SerialPortHelper {

    private SerialPortManager mSerialPortManager;
    private Device mDevice;
    private OnGetData onGetData;

    public interface OnGetData{
        void onDataReceived(byte[] bytes);
    }

    public SerialPortHelper(OnGetData onGetData) {
        this.onGetData = onGetData;
    }

    public void openSerialPort() {
        try {
            closeSerialPort();

            SerialPortFinder serialPortFinder = new SerialPortFinder();
            ArrayList<Device> devices = serialPortFinder.getDevices();//获取串口设备列表集合
            System.out.println("获取到节点数量为：" + devices.size());
            String ss=new Gson().toJson(devices);
            Log.i("SerialPortHelper","devices列表数据："+ss);
            mSerialPortManager = new SerialPortManager();

            //添加数据通信监听
            mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
                @Override
                public void onDataReceived(byte[] bytes) {
                    System.out.println("接收数据：" + Arrays.toString(bytes));
                    Log.i("SerialPortHelper","收到数据：" + Arrays.toString(bytes));
                    if (onGetData != null) {
                        onGetData.onDataReceived(bytes);
                    }
                }

                @Override
                public void onDataSent(byte[] bytes) {
                    System.out.println("发送数据：" + Arrays.toString(bytes));
                    Log.i("SerialPortHelper","发送数据：" + Arrays.toString(bytes));
                }
            });

            //添加打开串口监听
            mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
                @Override
                public void onSuccess(File device) {
                    System.out.print("串口链接成功，节点为：");
                    System.out.println(device.getName());//打印节点ID
                    Log.i("SerialPortHelper","串口链接成功: "+device.getName());
                }

                @Override
                public void onFail(File device, Status status) {
                    System.out.print("串口链接失败，节点为：");
                    System.out.println(device.getName() + status);
                    Log.i("SerialPortHelper","串口链接失败: "+device.getName());
                }
            });


            /*打开串口
            * 参数1：串口
              参数2：波特率
              返回：串口打开是否成功*/
            for (Device dev : devices) {
                if ("ttyS2".equalsIgnoreCase(dev.getName())) {
                    boolean openSerialPort = mSerialPortManager.openSerialPort(dev.getFile(), 9600);
                    if (openSerialPort) {
                        mDevice = dev;
                        /*发送数据
                          参数：发送数据 byte[]
                          返回：发送是否成功*/
                        mSerialPortManager.sendBytes(new byte[]{0X02, 0X52, 0X53, 0X45, 0X40, (byte) 0XAA, 0X0D});
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void zeroSet() {
        try {
            if (mSerialPortManager != null) {
                mSerialPortManager.sendBytes(zero);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    byte[] read = new byte[]{0X02, 0X52, 0X53, 0X45, 0X40, (byte) 0XAA, 0X0D};
    // 零点标定
    byte[] zero = new byte[]{0x02, 0x52, 0x5A, 0x52, 0x7F, (byte)0xFD, 0x0D};
    // 设置分度值0.1kg 最大称量600kg - 测试不通过
    byte[] jd1 = new byte[]{0x02, 0x53, 0x45, 0x54, 0x40, 0x20, 0x36, 0x30, 0x30, 0x2E, 0x30, 0x30, 0x69, 0x30, 0x31, 0x32, (byte)0xEC, 0x0D};
    // 设置设分度值0.05kg 最大称量600kg - 测试没问题
    byte[] jd05 = new byte[]{0x02, 0x53, 0x45, 0x54, 0x40, 0x20, 0x36, 0x30, 0x30, 0x2E, 0x30, 0x30, 0x68, 0x30, 0x31, 0x32, (byte)0xEB, 0x0D};
    // 设置分度值0.02kg 最大称量200kg - 测试没问题
    byte[] jd02 = new byte[]{0x02, 0x53, 0x45, 0x54, 0x40, 0x20, 0x32, 0x30, 0x30, 0x2E, 0x30, 0x30, 0x67, 0x30, 0x31, 0x32, (byte)0xE6, 0x0D};


    public void jiaoZhun(int i) {

        if(mSerialPortManager != null && mDevice != null) {
//            boolean openSerialPort = mSerialPortManager.openSerialPort(mDevice.getFile(), 9600);
//            if (openSerialPort) {
            try {
                Thread.sleep(1000);
                System.out.println("发送读取命令："+mSerialPortManager.sendBytes(read));
                Thread.sleep(1000);
                if(i == 1) {
                    System.out.println("发送校准命令：" + mSerialPortManager.sendBytes(jd1));
                }
                if(i == 2) {
                    System.out.println("发送校准命令：" + mSerialPortManager.sendBytes(jd05));
                }
                if(i == 3) {
                    System.out.println("发送校准命令：" + mSerialPortManager.sendBytes(jd02));
                }
                Thread.sleep(1000);
                System.out.println("发送零点标定命令："+mSerialPortManager.sendBytes(zero));
                Thread.sleep(1000);//给点时间用来接收
                System.out.println("校准结束测试");
//                    mSerialPortManager.closeSerialPort();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            }
        }
    }

    byte[] fm25 = new byte[]{0X02, 0X43, 0X41, 0X53, 0X7F, 0X20, 0X20, 0X32, 0X35, 0X2E, 0X30, 0X30, (byte) 0X8B, 0X0D,};
    // 放置砝码
    public void putFaMa() {
        if(mSerialPortManager != null && mDevice != null) {
//            boolean openSerialPort = mSerialPortManager.openSerialPort(mDevice.getFile(), 9600);
//            if (openSerialPort) {
            System.out.println("发送放置砝码命令："+mSerialPortManager.sendBytes(fm25));
            try {
                Thread.sleep(1000);//给点时间用来接收
                System.out.println("放置砝码结束测试");
//                    mSerialPortManager.closeSerialPort();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            }
        }
    }

    public void closeSerialPort() {
        try {
            if(mSerialPortManager != null) {
                mSerialPortManager.closeSerialPort();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mSerialPortManager = null;
            mDevice = null;
        }
    }
}
