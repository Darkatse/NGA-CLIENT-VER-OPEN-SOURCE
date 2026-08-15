package sp.phone.util;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.common.util.NLog;
import sp.phone.http.bean.StringFindResult;

@SuppressLint("SimpleDateFormat")
public class StringUtils {
    public final static String key = "asdfasdf";

    private static final String[] SAYING = ContextUtils.getResources().getStringArray(R.array.saying);

    /**
     * 验证是否是邮箱
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email))
            return false;
        String pattern1 = "^([a-z0-9A-Z]+[-_|\\.]?)+[a-z0-9A-Z_]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(pattern1);
        Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断是否是 "" 或者 null
     */
    public static boolean isEmpty(String str) {
        if (str != null && !"".equals(str)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * yy-M-dd hh:mm
     */
    public static Long sDateToLong(String sDate) {
        DateFormat df = new SimpleDateFormat("yy-M-dd hh:mm");
        Date date = null;
        try {
            date = df.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static boolean isNumer(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static Long parseLong(String str) {
        if (str == null) {
            return null;
        } else {
            if (str.equals("")) {
                return 0l;
            } else {
                return Long.parseLong(str);
            }
        }
    }

    public static String encodeUrl(final String s, final String charset) {

        /*
         * try { return java.net.URLEncoder.encode(s,charset); // this not work
         * in android 4.4 if a english char is followed //by a Chinese character
         *
         * } catch (UnsupportedEncodingException e) {
         *
         * return ""; }
         */
        String ret = UriEncoderWithCharset.encode(s, null, charset);
        // NLog.i("111111", s+"----->"+ret);
        return ret;
    }


    public static String removeBrTag(String s) {
        s = s.replaceAll("<br/><br/>", "\n");
        s = s.replaceAll("<br/>", "\n");
        return s;
    }

    public static String getSaying() {
        Random random = new Random();
        int num = random.nextInt(SAYING.length);
        return SAYING[num];
    }

    public static String unEscapeHtml(String s) {
        return StringHelper.unescapeHTML(s);
    }

    public static StringFindResult getStringBetween(String data, int begPosition, String startStr, String endStr) {
        StringFindResult ret = new StringFindResult();
        do {
            if (isEmpty(data) || begPosition < 0
                    || data.length() <= begPosition || isEmpty(startStr)
                    || isEmpty(startStr))
                break;

            int start = data.indexOf(startStr, begPosition);
            if (start == -1)
                break;

            start += startStr.length();
            int end = data.indexOf(endStr, start);
            if (end == -1)
                end = data.length();
            ret.result = data.substring(start, end);
            ret.position = end + endStr.length();

        } while (false);

        return ret;
    }

    public static String toBinaryArray(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++) {
            builder.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        }
        return builder.toString();
    }

    public static int getUrlParameter(String url, String paraName) {
        if (StringUtils.isEmpty(url)) {
            return 0;
        }
        final String pattern = paraName + "=";
        int start = url.indexOf(pattern);
        if (start == -1)
            return 0;
        start += pattern.length();
        int end = url.indexOf("&", start);
        if (end == -1)
            end = url.length();
        String value = url.substring(start, end);
        int ret = 0;
        try {
            ret = Integer.parseInt(value);
        } catch (Exception e) {
            NLog.e("getUrlParameter", "invalid url:" + url);
        }

        return ret;
    }

    public static String timeStamp2Date1(String timeStamp) {
        return timeStamp2Date(timeStamp, "yyyy-MM-dd HH:mm:ss");
    }

    public static String timeStamp2Date2(String timeStamp) {
        return timeStamp2Date(timeStamp, "MM-dd HH:mm");
    }

    public static String timeStamp2Date(String timeStamp, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeStamp) * 1000);
        return new SimpleDateFormat(format, Locale.getDefault()).format(calendar.getTime());
    }

    public static String getStringFromAssets(String path) {
        AssetManager assetManager = ContextUtils.getContext().getAssets();
        try (InputStream is = assetManager.open(path)) {
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            return new String(buffer, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}