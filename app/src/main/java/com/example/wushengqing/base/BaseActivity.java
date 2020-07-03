/*
 * Copyright 2018-2020 KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.wushengqing.base;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.AdaptScreenUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.example.wushengqing.data.net.NetWorkManager;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;



public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends RxAppCompatActivity implements LifecycleProvider<ActivityEvent> {
    protected V binding;
    protected VM viewModel;
    private int viewModelId;

    private ViewModelProvider mActivityProvider;
    private KProgressHUD kProgressHUD;


    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    public VM initViewModel() {
        return null;
    }


    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    public abstract int initContentView(Bundle savedInstanceState);

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    public abstract int initVariableId();


    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化界面观察者的监听
     */
    protected abstract void initViewObservable();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);
        BarUtils.setStatusBarLightMode(this, true);
        //私有的初始化DataBinding和ViewModel方法
        initViewDataBinding(savedInstanceState);
        //私有的ViewModel与View的契约事件回调逻辑
        registerUIChangeLiveDataCallBack();
        initData();
        initViewObservable();
        //注册RxBus
        viewModel.registerRxBus();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除Messenger注册
//        Messenger.getDefault().unregister(viewModel);
        if (viewModel != null) {
            viewModel.removeRxBus();
        }
        if (binding != null) {
            binding.unbind();
        }
        NetWorkManager.getInstance().reMoveRequest(getClass().getSimpleName());
    }

    //刷新布局
    public void refreshLayout() {
        if (viewModel != null) {
            binding.setVariable(viewModelId, viewModel);
        }
    }

    public boolean isDebug() {
        return getApplicationContext().getApplicationInfo() != null &&
                (getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    @Override
    public Resources getResources() {
        if (ScreenUtils.isPortrait()) {
            return AdaptScreenUtils.adaptWidth(super.getResources(), 360);
        } else {
            return AdaptScreenUtils.adaptHeight(super.getResources(), 640);
        }
    }

    protected void showLongToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    protected void showShortToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(int stringRes) {
        showLongToast(getApplicationContext().getString(stringRes));
    }

    protected void showShortToast(int stringRes) {
        showShortToast(getApplicationContext().getString(stringRes));
    }





    /**
     * 注入绑定
     */
    private void initViewDataBinding(Bundle savedInstanceState) {
        //DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }, 同步后会自动关联android.databinding包
        binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState));
        viewModelId = initVariableId();
        viewModel = initViewModel();
        if (viewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel.class;
            }
            viewModel = (VM) createViewModel(modelClass);
        }
        //关联ViewModel
        binding.setVariable(viewModelId, viewModel);
        //支持LiveData绑定xml，数据改变，UI自动会更新
        binding.setLifecycleOwner(this);
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(viewModel);

        //注入RxLifecycle生命周期
        NetWorkManager.getInstance().injectLifecycleProvider(this);
    }

    /**
     * 创建ViewModel
     *
     * @param <T>
     * @return
     */
    protected <T extends ViewModel> T createViewModel(@NonNull Class<T> modelClass) {
        if (mActivityProvider == null) {
            mActivityProvider = new ViewModelProvider(this);
        }
        return mActivityProvider.get(modelClass);
    }


    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle);
        }
        startActivity(intent);
    }


    //注册ViewModel与View的契约UI回调事件
    protected void registerUIChangeLiveDataCallBack() {
        //加载对话框显示
        NetWorkManager.getInstance().getUC().getShowDialogEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                showDialog();
            }
        });
        //加载对话框消失
        NetWorkManager.getInstance().getUC().getDismissDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                dismissDialog();
            }
        });
    }


    public void showDialog() {
        if (kProgressHUD == null) {
            kProgressHUD = KProgressHUD.create(BaseActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
        }else {
            kProgressHUD.show();
        }
    }



    public void showDialog(boolean isCanCell) {
        if (kProgressHUD == null) {
            kProgressHUD = KProgressHUD.create(BaseActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setCancellable(isCanCell)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
        }else {
            kProgressHUD.show();
        }
    }

    public void dismissDialog() {
        if (kProgressHUD != null && kProgressHUD.isShowing()) {
            kProgressHUD.dismiss();
        }
    }




}
