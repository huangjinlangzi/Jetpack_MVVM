package com.example.wushengqing.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.example.wushengqing.base.BaseViewModel;
import com.example.wushengqing.data.entity.Result;
import com.example.wushengqing.data.entity.UserBean;
import com.example.wushengqing.data.repository.DataRepository;


import java.util.HashMap;
import java.util.Map;



public class LoginViewModel extends BaseViewModel {
    //用户名的绑定
    public ObservableField<String> userName = new ObservableField<>("");
    public ObservableField<String> userPassword = new ObservableField<>("");
    private MutableLiveData<Result<UserBean>> userBeanObservable;

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }



    public MutableLiveData<Result<UserBean>> getUserBeanObservable(){
        userBeanObservable = new MutableLiveData<Result<UserBean>>();
        return userBeanObservable;
    }


    public void requestLogin(String tag){
        Map<String,Object> param = new HashMap();
        param.put("username",userName.get());
        param.put("password",userPassword.get());

        DataRepository.getInstance().login(param,userBeanObservable);

    }




}
