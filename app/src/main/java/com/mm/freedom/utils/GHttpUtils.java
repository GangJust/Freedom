package com.mm.freedom.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GHttpUtils {
    /**
     * GET请求
     *
     * @param sourceUrl 目标地址
     * @return 文本内容
     */
    public static String get(String sourceUrl) {
        return get(sourceUrl, "");
    }

    /**
     * GET请求
     *
     * @param sourceUrl 目标地址
     * @param params    参数
     * @return 文本内容
     */
    public static String get(String sourceUrl, String params) {
        sourceUrl = sourceUrl.contains("?") ? (sourceUrl + "&" + params) : (sourceUrl + "?" + params);
        StringBuilder builder = new StringBuilder();
        try {
            URL url = new URL(sourceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            InputStreamReader inputStream = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
            int b;
            while ((b = inputStream.read()) != -1) builder.append((char) b);
            inputStream.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * 获取重定向地址
     *
     * @param sourceUrl 目标地址
     * @return 重定向后的地址
     */
    public static String getRedirectsUrl(String sourceUrl) {
        return getRedirectsUrl(sourceUrl, true);
    }

    /**
     * 获取重定向地址
     *
     * @param sourceUrl 目标地址
     * @param finish    是否等待内容加载完毕后获取最终地址, true 可以获取多层302, false 只能获取一层
     * @return 重定向后的地址
     */
    public static String getRedirectsUrl(String sourceUrl, boolean finish) {
        String redirectsUrl = "";
        try {
            URL url = new URL(sourceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (!finish) {
                conn.setInstanceFollowRedirects(false);
                conn.connect();
                if (conn.getResponseCode() == 302) redirectsUrl = conn.getHeaderField("Location");
                conn.disconnect();
            } else {
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                redirectsUrl = conn.getURL().toString();
                inputStream.close();
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return redirectsUrl;
    }

    /**
     * 下载文件
     *
     * @param sourceUrl
     * @param saveAbsolutePath
     * @return
     */
    public static boolean download(String sourceUrl, String saveAbsolutePath, String filename, DownloadListening downloadListening) {
        File file = new File(saveAbsolutePath);
        File outFile = null;
        if (!file.exists()) {
            if (file.mkdirs()) outFile = new File(file, filename);
        } else {
            outFile = new File(file, filename);
        }

        if (outFile != null) {
            try {
                URL url = new URL(sourceUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //conn.setRequestProperty("Connection","keep-alive");
                long total = conn.getContentLength();
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                FileOutputStream writer = new FileOutputStream(outFile);
                long realCount = 0;
                int len;
                byte[] bytes = new byte[1024];
                while ((len = inputStream.read(bytes)) != -1) {
                    realCount += len;
                    writer.write(bytes, 0, len);
                    downloadListening.onProgress(realCount, total); //回调进度
                }
                writer.flush();
                writer.close();
                conn.disconnect();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public interface DownloadListening {
        void onProgress(long real, long total);
    }
}
