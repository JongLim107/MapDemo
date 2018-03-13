package com.brightoil.mapdemo.tools;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Mr.Jude on 2015/2/12.
 * 管理Activity的类
 */
public class AppManager {

    private static Stack<Activity> activityStack = new Stack<>();

    private static final JActivityLifecycleCallbacks instance = new JActivityLifecycleCallbacks();

    private static class JActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            activityStack.remove(activity);
            activityStack.push(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            activityStack.remove(activity);
        }
    }

    public static Application.ActivityLifecycleCallbacks getActivityLifecycleCallbacks() {
        return instance;
    }

    public static Stack<Activity> getActivityStack() {
        Stack<Activity> stack = new Stack<>();
        stack.addAll(activityStack);
        return stack;
    }

    public static void closeAllActivity() {
        while (true) {
            Activity activity = currentActivity();
            if (null == activity) {
                break;
            }
            closeActivity(activity);
        }
    }

    public static void closeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    public static void closeActivityByName(String name) {
        int index = activityStack.size() - 1;

        while (true) {
            Activity activity = activityStack.get(index);

            if (null == activity) {
                break;
            }

            String activityName = activity.getClass().getName();
            if (!TextUtils.equals(name, activityName)) {
                index--;
                if (index < 0) {//avoid index out of bound.
                    break;
                }
                continue;
            }
            closeActivity(activity);
            break;
        }
    }

    public static void closeActivityExceptName(String name) {
        List<Activity> list = new ArrayList<>();

        int index = activityStack.size() - 1;
        while (true) {
            Activity activity = activityStack.get(index);

            if (null == activity) {
                continue;
            }

            String activityName = activity.getClass().getName();
            if (!TextUtils.equals(name, activityName)) {
                list.add(activity);

                index--;
                if (index < 0) {//avoid index out of bound.
                    break;
                }
                continue;
            }
        }

        for (Activity activity : list) {
            activity.finish();
        }

        list.clear();
    }

    public static boolean contains(Class<? extends Activity> clazz) {
        if (activityStack.isEmpty()) {
            return false;
        }

        for (Activity activity : activityStack) {
            final Class<? extends Activity> aClass = activity.getClass();
            if (aClass == clazz) {
                return true;
            }
        }

        return false;
    }

    public static Activity currentActivity() {
        Activity activity = null;
        if (!activityStack.isEmpty()) {
            activity = activityStack.peek();
        }
        return activity;
    }

    public static String getCurrentActivityName() {
        Activity activity = currentActivity();
        String name = "";
        if (activity != null) {
            name = activity.getClass().getName();
        }
        return name;
    }

}