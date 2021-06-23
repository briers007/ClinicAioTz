package com.minorfish.clinicwaste.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * Author: HueYoung
 * E-mail: yangtaolue@xuechengjf.com
 * Date: 2015/3/20.
 * <p/>
 * Description : 主页
 */
public class NetUtil {
	/**
	 * 检查用户的网络:是否有网络
	 */
	public static boolean checkNet(Context context) {
		// 判断：WIFI链接
		boolean isWIFI = isWIFIConnection(context);
		// 判断：Mobile链接
		boolean isMOBILE = isMOBILEConnection(context);


		if (!isWIFI && !isMOBILE) {
			return false;
		}

		return true;
	}


	/**
	 * 判断：Mobile链接
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isMOBILEConnection(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	//判断当前网络状态是否连接函数
	private static Boolean isNetworkConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);

		if (manager == null) {
			return false;
		}

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}

		return true;
	}

	/**
	 * 判断：WIFI链接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWIFIConnection(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	/**
	 * 检测网络状态是否联通
	 *
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (null != info && info.isConnected() && info.isAvailable()) {
				return true;
			}
		} catch (Exception e) {
			Log.e("isNetworkAvailable", "current network is not available");
			return false;
		}
		return false;
	}
}
