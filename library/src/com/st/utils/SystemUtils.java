package com.st.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 系统工具类
 */
public class SystemUtils {

    private static final String TAG = SystemUtils.class.getSimpleName();

    public static boolean locationService(final Activity context) {
        boolean locationStatus = false;
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationStatus = true;
        }

        if (!locationStatus) {
            Builder b = new AlertDialog.Builder(context).setTitle("提示").setMessage(
                    "启用\"定位服务\"来允许\"来往\"确定您的位置");
            b.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            }).show();
        }
        return locationStatus;
    }

    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode;
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return verName;
    }

    /**
     * 获取手机的设备ID即IMEI
     * 
     * <pre>
     * 需要加入
     * android.permission.READ_PHONE_STATE
     * </pre>
     * 
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取Wifi Mac 地址
     * 
     * <pre>
     * 需要加入
     * uses-permission android.permission.ACCESS_WIFI_STATE
     * </pre>
     * 
     * @param context
     * @return 如果没有wifi则可能为空
     */
    public static String getWifiMacAddress(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            return info.getMacAddress();
        }
        return null;
    }

    /**
     * 获取Wifi 的BSSID
     * 
     * <pre>
     * 需要加入
     * uses-permission android.permission.ACCESS_WIFI_STATE
     * </pre>
     * 
     * @param context
     * @return
     */
    public static String getWifiBSSID(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            return info.getBSSID();
        }
        return null;
    }

    /**
     * 获取设备的IP地址
     * 
     * <pre>
     * 需要加入
     * uses-permission android.permission.INTERNET
     * </pre>
     * 
     * @return
     */
    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 获取设备的型号，如(MI-ONE C1)
     * 
     * @return
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 系统版本号
     */
    public static String getSystemVersion() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    public static PackageInfo getPackageInfo(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        try {
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            return packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 振动
     * 
     * @param context
     * @param timelong 振动的时间长度，单位为毫秒
     */
    public static void playVibator(Context context, long timelong) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if (vib != null) {
            vib.vibrate(timelong);
        }
    }

    public final static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    public static String getUrl(final String url) {
        String realUrl = url;

        if (!realUrl.startsWith("http://")) {
            realUrl = "file://" + url;
        }
        return realUrl;
    }
}
