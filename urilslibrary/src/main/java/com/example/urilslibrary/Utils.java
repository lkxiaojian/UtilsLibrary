package com.example.urilslibrary;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by lk on 2017/12/31.
 */

public class Utils {


    //使用正则表达式判断电话号码
    public static boolean isMobileNO(String tel) {
        Pattern p = Pattern.compile("^(13[0-9]|15([0-3]|[5-9])|14[5,7,9]|17[1,3,5,6,7,8]|18[0-9]|19[0-9])\\d{8}$");
        Matcher m = p.matcher(tel);
        return m.matches();
    }

    /**
     * 时间转换
     *
     * @param millisecond
     * @return
     */
    public static String intToStr(int millisecond) {
        int second = millisecond / 1000; //总共换算的秒
        int hh = second / 3600; //小时
        int mm = second % 3600 / 60; //分钟
        int ss = second % 60; //时分秒中的秒的得数
        String str = null;
        if (hh != 0) { //如果是个位数的话，前面可以加0 时分秒
            str = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            str = String.format("%02d:%02d", mm, ss);
        }
        return str;
    }

    /**
     * toast long
     *
     * @param context
     * @param message
     */

    public static void ToastLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * toast short
     *
     * @param context
     * @param message
     */

    public static void ToastShort(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static String getuuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 比较两个时间的先后顺序
     *
     * @param locatime
     * @param servertime
     * @return
     */
    public static boolean compareTime(String locatime, String servertime) {
        try {
            // 如果下载的时间戳为空
            if (servertime == null || servertime.equals("")) {
                return true;
            }
            // 如果本地时间为空 则说明该说明在本地没有任何操作
            if ((locatime == null || locatime.equals("")) && servertime != null && !servertime.equals("")) {
                return false;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date date1 = format.parse(locatime);
            Date date2 = format.parse(servertime);
            boolean flag = date1.after(date2);
            return flag;
        } catch (Exception e) {
            return true;
        }

    }

    /**
     * 以原点为圆点，以radius维半径画圆，通过弧度o,获得坐标
     *
     * @param radius 半径
     * @param o      弧度
     * @return
     */
    public static float[] getXYPoint(float[] centrePoint, int radius, float o) {
        float[] xyPoint = {(float) (radius * Math.sin(o) + centrePoint[0]), (float) ((-1) * radius * Math.cos(o) + centrePoint[1])};
        return xyPoint;
    }

    /**
     * 对文件重命名
     *
     * @param filePath 文件的路径
     */
    public static String chageFileName(String filePath, String reName, Context mcontext) {
        File file = new File(filePath);
        //前面路径必须一样才能修改成功
        String path = filePath.substring(0, filePath.lastIndexOf("/") + 1) + reName + filePath.substring(filePath.lastIndexOf("."), filePath.length());
        File newFile = new File(path);
        boolean b = file.renameTo(newFile);
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(path));
        mediaScanIntent.setData(contentUri);
        mcontext.sendBroadcast(mediaScanIntent);
        Intent mediaScanIntent2 = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri2 = Uri.fromFile(new File(filePath));
        mediaScanIntent2.setData(contentUri2);
        mediaScanIntent2.setData(contentUri2);
        mcontext.sendBroadcast(mediaScanIntent2);
        return path;
    }


    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {

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
     * 判断网络是否可以  不可以   进行吐司
     *
     * @param context
     */
    public static boolean IsNetwork(Context context) {
        boolean networkAvailable = isNetworkAvailable(context);
        if (!networkAvailable) {
            ToastShort(context, "当前网络不可用");
            return false;
        }
        return true;

    }


    /**
     * 获取网络状态 是否是wifi
     *
     * @param context
     * @return one of TYPE_NONE, TYPE_MOBILE, TYPE_WIFI
     * @permission android.permission.ACCESS_NETWORK_STATE
     */
    public static final int TYPE_NONE = -1;//没有联网
    public static final int TYPE_MOBILE = 0;//移动的数据
    public static final int TYPE_WIFI = 1;//wifi

    public static final int getNetWorkStates(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return TYPE_NONE;//没网
        }

        int type = activeNetworkInfo.getType();
        switch (type) {
            case ConnectivityManager.TYPE_MOBILE:
                return TYPE_MOBILE;//移动数据
            case ConnectivityManager.TYPE_WIFI:
                return TYPE_WIFI;//WIFI
            default:
                break;
        }
        return TYPE_NONE;
    }

    /**
     * Transform map to String.
     *
     * @param map
     * @return
     */
    public static String map2String(Map<String, String> map) {
        String str = null;
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue()).append("&");
        }
        str = sb.deleteCharAt(sb.length() - 1).toString();
        return str;
    }

    /**
     * 获取手机IMEI号
     * <p>
     * 需要动态权限: android.permission.READ_PHONE_STATE
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission")
        String imei = telephonyManager.getDeviceId();

        return imei;
    }


    /**
     * 打开相册返回的结果
     *
     * @param data
     * @param context
     * @return
     */
    public static String handleCamera(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    /**
     * 生成UUID
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }

    /**
     * 位数不够补0
     */
    public static String patchzero(String val) {
        return val = val != null && val.length() < 2 ? "0" + val : val;
    }

    /**
     * 获取视频时长
     *
     * @param path
     * @return
     */
    public static long getVideoLength(String path) {
        MediaPlayer player = new MediaPlayer();
        File file = new File(path);
        try {
            player.setDataSource(file.getPath());
            player.prepare();
            long duration = player.getDuration();//获取音频的时间
            player.release();//记得释放资源
            return duration;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("e", "e----" + e);
        }
        return 0;
    }


    /**
     * 获取视频时长
     *
     * @param mUri
     * @return
     */
    public static String getRingDuring(String mUri) {
        String duration = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();

        try {
            if (mUri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 7.0; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            }

            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
            Log.e("ex", "ex--获取时长失败--" + ex);
        } finally {
            mmr.release();
        }
        return duration;


    }

    /**
     * 根据地质，获取文件名
     *
     * @param path
     * @return
     */
    public static String getsqlit(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        String[] split = path.split("/");
        String s = split[split.length - 1];
        return s;
    }

    /**
     * 18位身份证校验,粗略的校验
     *
     * @param idCard
     * @return
     * @author lyl
     */
    public static boolean is18ByteIdCard(String idCard) {
        Pattern pattern1 = Pattern.compile("^(\\d{6})(19|20)(\\d{2})(1[0-2]|0[1-9])(0[1-9]|[1-2][0-9]|3[0-1])(\\d{3})(\\d|X|x)?$"); //粗略的校验
        Matcher matcher = pattern1.matcher(idCard);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }


}
