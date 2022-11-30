package com.mm.freedom.config;

import com.mm.freedom.utils.GLogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 错误/异常, 记录日志
 */
public class ErrorLog {
    private static final ErrorLog errorLog = new ErrorLog();
    private static String logParentPath = "";

    public static void init(String logParentPath) {
        ErrorLog.logParentPath = logParentPath;
    }

    public static ErrorLog getInstance() {
        if (logParentPath == null || logParentPath.isEmpty())
            throw new RuntimeException("Please set `logParentPath`, see at:  Error.init(logParentPath)");
        return errorLog;
    }

    /**
     * @param exception 想要记录的异常信息
     */
    public void exceptionLog(String path, Exception exception) {
        try {
            File logPath = new File(logParentPath, path);
            if (!logPath.exists())
                if (!logPath.mkdirs())
                    return;

            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss", Locale.getDefault());
            File file = new File(logPath, format.format(date) + ".log");
            FileOutputStream writer = new FileOutputStream(file);
            PrintWriter printWriter = new PrintWriter(writer);
            exception.printStackTrace(printWriter);
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
