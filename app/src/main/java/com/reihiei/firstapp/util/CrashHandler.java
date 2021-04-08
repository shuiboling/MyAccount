package com.reihiei.firstapp.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private Context context;
    private static CrashHandler instance;

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

        autoDelete(5);
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        String path = getRootFileName();
        File dir = new File(path);
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            String fileName = getRootFileName() + File.separator + "crash" + date + ".log";
            PrintStream printStream = new PrintStream(fileName);
            e.printStackTrace(printStream);
            printStream.flush();
            printStream.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            if (uncaughtExceptionHandler != null) {
                uncaughtExceptionHandler.uncaughtException(t, e);
            }
        }
    }

    private String getRootFileName(){
        return context.getExternalFilesDir(null) + File.separator + "crash";
    }

    private void autoDelete(int delDay) {
        deleteFiles(getRootFileName(), new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                //获取文件名称日期部分
                Log.d("zyy name",name);

                String fileDate = getFileDate(name);
                int day = delDay < 0 ? delDay : -1 * delDay;
                String date = getOtherDay(day);

                return date.compareTo(fileDate)>=0;
            }
        });
    }

    private void deleteFiles(String fileName, FilenameFilter filenameFilter) {

        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else if (!file.isDirectory()) {
            return;
        }
        File[] files;
        if (filenameFilter != null) {
            files = file.listFiles(filenameFilter);
        } else {
            files = file.listFiles();
        }
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isFile()) {
                f.delete();
            }
        }
    }

    private String getFileDate(String name) {
        if(TextUtils.isEmpty(name)){
            return "";
        }
        String withoutCrash = name.replace("crash","");
        String date = withoutCrash.substring(0,withoutCrash.indexOf("."));
        return date;
    }

    private String getOtherDay(int day) {
        Calendar calendar = Calendar.getInstance();
        //获取前day天的日期
        calendar.add(Calendar.DATE,day);
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}
