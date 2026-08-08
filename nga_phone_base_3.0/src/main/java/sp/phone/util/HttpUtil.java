package sp.phone.util;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import gov.anzong.androidnga.common.util.AppEnvironment;
import gov.anzong.androidnga.common.util.NLog;

public class HttpUtil {

    public static final String NGA_ATTACHMENT_HOST = "img.nga.178.com"; //img.ngacn.cc";
    private static final String[] servers = {"https://nga.178.com", "https://bbs.ngacn.cc"};
    private static final String TAG = HttpUtil.class.getSimpleName();
    private static final String[] host_arr = {};
    public static String PATH = AppEnvironment.getExternalStoragePictureDirectory() + "/nga_cache";
    public static String PATH_AVATAR = PATH + "/nga_cache";

    public static String Server = "https://bbs.nga.cn";
    public static String HOST = "";

    @SuppressWarnings("unused")
    public static void selectServer2() {
        for (String host : host_arr) {
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(host).openConnection();
                conn.setConnectTimeout(6000);
                int result = conn.getResponseCode();
                String re = conn.getResponseMessage();
                if (result == HttpURLConnection.HTTP_OK) {
                    HOST = host;//
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }
            }

        }
    }

    public static void switchServer() {
        int i = 0;
        for (; i < servers.length; ++i) {
            if (Server.equals(servers[i]))
                break;
        }
        i = (i + 1) % servers.length;
        Server = servers[i];
    }

    public static void downImage(String uri, String fileName) {
        try {
            URL url = new URL(uri);
            File file = new File(fileName);

            FileUtils.copyURLToFile(url, file, 2000, 5000);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            NLog.e(TAG, "failed to download img:" + uri + "," + e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private static void writeFile(URL url, String fileName) {
        try {
            FileUtils.copyURLToFile(url, new File(fileName), 4000, 3000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
