package com.example.wushengqing.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.multidex.MultiDex;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.LogUtils;

import com.example.wushengqing.data.entity.UserBean;
import com.example.wushengqing.utils.Utils;
import com.google.android.gms.common.util.ProcessUtils;

import java.util.ArrayList;
import java.util.List;





public class App extends Application implements ViewModelStoreOwner {
    protected static Application mContext;
    protected static List<Activity> actList = new ArrayList<Activity>();
    private static App myApplication;

    // 实现全应用范围内的 生命周期安全 且 事件源可追溯的 视图控制器 事件通知。

    private ViewModelStore mAppViewModelStore;

    private ViewModelProvider.Factory mFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        mAppViewModelStore = new ViewModelStore();

        Utils.init(this);

        LogUtils.d("PROCESS", ProcessUtils.getMyProcessName());

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Application getContext() {
        return mContext;
    }

    public static App getInstance() {
        if (myApplication == null) {
            myApplication = new App();
        }
        return myApplication;
    }


    public void saveUserBean(UserBean userBean){
        CacheDiskUtils.getInstance().put("user",userBean);
    }

    public UserBean getUserBean(){
        UserBean userBean = (UserBean) CacheDiskUtils.getInstance().getSerializable("user");
        return userBean;
    }

    public void clearUserBean(){
        CacheDiskUtils.getInstance().remove("user");
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mAppViewModelStore;
    }

    public ViewModelProvider getAppViewModelProvider(Activity activity) {
        return new ViewModelProvider((App) activity.getApplicationContext(),
                ((App) activity.getApplicationContext()).getAppFactory(activity));
    }

    private ViewModelProvider.Factory getAppFactory(Activity activity) {
        Application application = checkApplication(activity);
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }
        return mFactory;
    }

    private Application checkApplication(Activity activity) {
        Application application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Your activity/fragment is not yet attached to "
                    + "Application. You can't request ViewModel before onCreate call.");
        }
        return application;
    }

    private Activity checkActivity(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            throw new IllegalStateException("Can't create ViewModelProvider for detached fragment");
        }
        return activity;
    }
}
