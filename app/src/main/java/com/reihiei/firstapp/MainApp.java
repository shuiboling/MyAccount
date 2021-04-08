package com.reihiei.firstapp;

import android.app.Activity;
import android.app.Application;

import com.reihiei.firstapp.util.CrashHandler;
import com.reihiei.firstapp.util.SpUtils;

import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    private static MainApp instance;
    List<Activity> activities = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        SpUtils.getInstance().init(getApplicationContext());
        CrashHandler.getInstance().init(getApplicationContext());
    }

    public static MainApp getInstance(){
        return instance;
    }

    public void addActivity(Activity activity){
        if(activities == null){
            activities = new ArrayList<>();
        }
        activities.add(activity);
    }

    public void exitApp(){
        if(activities != null){
            synchronized (activities) {
                for(Activity activity:activities){
                    activity.finish();
                }
            }
        }

        System.exit(0);
    }
}
