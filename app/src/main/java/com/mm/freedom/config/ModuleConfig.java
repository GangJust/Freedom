package com.mm.freedom.config;

import android.content.Context;
import android.net.LocalServerSocket;
import android.os.Environment;

import com.mm.freedom.utils.GJSONUtils;
import com.mm.freedom.utils.GPathUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ModuleConfig {
    public static final Byte[] lock = new Byte[0];

    //获取模块设置
    public static void getModuleConfig(Context context, ConfigCallback callback) {
        synchronized (lock) {
            new Thread(() -> {
                try {
                    File settingFile = getModuleSettingFile(context);
                    if (!settingFile.exists()) return;

                    FileInputStream inputStream = new FileInputStream(settingFile);
                    StringBuilder builder = new StringBuilder();
                    int b;
                    while ((b = inputStream.read()) != -1) builder.append((char) b);
                    inputStream.close();

                    //解析设置文件
                    JSONObject parse = GJSONUtils.parse(builder.toString());
                    Config config = new Config();
                    config.setCustomDownloadValue(GJSONUtils.getBoolean(parse, "customDownload"));
                    config.setClipDataDetailValue(GJSONUtils.getBoolean(parse, "clipDataDetail"));
                    config.setSaveEmojiValue(GJSONUtils.getBoolean(parse, "saveEmoji"));
                    config.setVersionName(GJSONUtils.getString(parse, "versionName"));
                    callback.callback(config);
                } catch (Exception e) {
                    e.printStackTrace();
                    //GLogUtil.xException(e.getMessage());
                }
            }).start();
        }
    }

    //保存模块设置
    public static void putModuleConfig(Context context, Config config) {
        synchronized (lock) {
            new Thread(() -> {
                try {
                    JSONObject setting = new JSONObject();
                    setting.put("customDownload", config.isCustomDownloadValue());
                    setting.put("clipDataDetail", config.isClipDataDetailValue());
                    setting.put("saveEmoji", config.isSaveEmojiValue());
                    setting.put("versionName", config.getVersionName());

                    FileOutputStream outputStream = new FileOutputStream(getModuleSettingFile(context));
                    outputStream.write(setting.toString().getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    //GLogUtil.xException(e.getMessage());
                }
            }).start();
        }
    }

    //获取模块设置文件路径
    public static File getModuleSettingFile(Context context) {
        File setting = new File(getModuleConfigDir(context), "setting.json");
        return setting;
    }

    //获取模块配置路径:(外置存储器/Download/Freedom/.config)
    public static File getModuleConfigDir(Context context) {
        File moduleDirectory = getModuleDirectory(context);
        File oldConfigDir = new File(moduleDirectory, "config");
        File newConfigDir = new File(moduleDirectory, ".config");
        //旧配置目录如果存在, 重命名为新配置目录
        if (oldConfigDir.exists()) oldConfigDir.renameTo(newConfigDir);
        if (!newConfigDir.exists()) newConfigDir.mkdirs();
        return newConfigDir;
    }

    //获取模块目录, 如果不存在则创建: (外置存储器/Download/Freedom/)
    public static File getModuleDirectory(Context context) {
        //首先外部存储器, Download目录下创建模块目录
        String storagePath = GPathUtils.getStoragePath(context);
        File moduleDir = new File(storagePath + "/" + Environment.DIRECTORY_DOWNLOADS + "/Freedom");
        if (!moduleDir.exists()) {
            //(创建失败) 没有外部存储器读写权限, 则私有目录下创建模块设置文件
            if (!moduleDir.mkdirs()) {
                return getModulePrivateDirectory(context);
            }
        } else {
            //否则目录存在创建一个临时文件, 测试是否具有读写权限
            File tempFile = new File(moduleDir, ".temp");
            try {
                if (tempFile.createNewFile()) {
                    tempFile.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
                //(创建失败) 没有外部存储器读写权限, 则私有目录下创建模块设置文件
                if (!moduleDir.mkdirs()) {
                    return getModulePrivateDirectory(context);
                }
            }
        }
        return moduleDir;
    }

    //获取模块目录下的子目录, 如果不存在则创建: (外置存储器/Download/Freedom/*)
    public static File getModuleDirectory(Context context, String childDir) {
        File moduleChildDir = new File(getModuleDirectory(context), childDir);
        if (!moduleChildDir.exists()) moduleChildDir.mkdirs();
        return moduleChildDir;
    }

    //获取模块目录, 私有目录: (外置存储器/Android/data/包名/files)
    public static File getModulePrivateDirectory(Context context) {
        return context.getExternalFilesDir("Freedom");
    }

    //获取模块目录, 私有目录: (外置存储器/Android/data/包名/files/*)
    public static File getModulePrivateDirectory(Context context, String childDir) {
        File modulePrivateChildDir = new File(getModulePrivateDirectory(context), childDir);
        if (!modulePrivateChildDir.exists()) modulePrivateChildDir.mkdirs();
        return modulePrivateChildDir;
    }

    public interface ConfigCallback {
        void callback(Config config);
    }
}
