package com.mm.freedom.utils;

import android.content.Context;
import android.net.LocalServerSocket;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;

/**
 * Android进程锁工具类
 */
public class GLockUtils {
    private static final Handler handler = new Handler(Looper.getMainLooper());
    public static final String defaultLockfileName = ".locked";

    /// 以下几个方法主要用在多个进程同时运行的情况, 只要文件锁存在, 则不让进程执行, 但是有个弊端, 程序非正常结束时无法对文件进行解锁(删除)操作.
    //开启文件锁, 在私有目录下创建一个空白文件, 锁存在或者成功创建均返回 true, 创建失败返回 false
    public static synchronized boolean fileLock(Context context) {
        return fileLock(context, defaultLockfileName);
    }

    public static synchronized boolean fileLock(Context context, String lockfileName) {
        try {
            if (hasFileLock(context, lockfileName)) return true;
            File externalFilesDir = context.getExternalFilesDir(null);
            File freedomLock = new File(externalFilesDir, lockfileName);
            return freedomLock.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //开启文件锁, 并在文件锁第一次被创建时回调 lockedCallback#locked 方法
    public static synchronized boolean fileLock(Context context, LockedCallback lockedCallback) {
        return fileLock(context, defaultLockfileName, lockedCallback);
    }

    public static synchronized boolean fileLock(Context context, String lockfileName, LockedCallback lockedCallback) {
        if (hasFileLock(context, lockfileName)) return true;
        boolean locked = fileLock(context, lockfileName);
        if (locked) handler.postDelayed(lockedCallback::locked, 1000);
        return locked;
    }

    //是否具有文件锁,
    public static synchronized boolean hasFileLock(Context context) {
        return hasFileLock(context, defaultLockfileName);
    }

    public static synchronized boolean hasFileLock(Context context, String lockfileName) {
        File externalFilesDir = context.getExternalFilesDir(null);
        File freedomLock = new File(externalFilesDir, lockfileName);
        return freedomLock.exists();
    }

    //解开文件锁, 锁不存在或者解锁成功删除均返回 true, 删除失败或出现异常返回 false
    public static synchronized boolean fileUnlock(Context context) {
        return fileUnlock(context, defaultLockfileName);
    }

    public static synchronized boolean fileUnlock(Context context, String lockfileName) {
        if (!hasFileLock(context, lockfileName)) return true;

        File externalFilesDir = context.getExternalFilesDir(null);
        File freedomLock = new File(externalFilesDir, lockfileName);
        try {
            return freedomLock.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /// 以下方法是以本地套字节方式加锁, 只要被监听的端口存在, 则停止访问, 应用结束后自动解锁(但是感觉并没什么大用, 多个进程下仍然会创建, 也可能是我使用时机不对.)
    public static synchronized boolean locateSocketLock(String lockName) {
        try {
            new LocalServerSocket(lockName);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static synchronized boolean locateSocketLock(String lockName, LockedCallback lockedCallback) {
        try {
            new LocalServerSocket(lockName);
            lockedCallback.locked();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //锁回调
    public interface LockedCallback {
        void locked();
    }
}
