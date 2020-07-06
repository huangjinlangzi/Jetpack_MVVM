package com.example.wushengqing.ui.login;

import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.wushengqing.BR;
import com.example.wushengqing.MainActivity;
import com.example.wushengqing.R;
import com.example.wushengqing.app.App;

import com.example.wushengqing.base.BaseActivity;
import com.example.wushengqing.data.entity.Result;
import com.example.wushengqing.databinding.ActivityLoginBinding;
import com.example.wushengqing.data.entity.UserBean;



public class LoginActivity extends BaseActivity<ActivityLoginBinding,LoginViewModel> {

    @Override
    public int initContentView(Bundle savedInstanceState) {

        return R.layout.activity_login;
    }

    @Override
    public int initVariableId() {
        return BR.vm;
    }



    @Override
    protected void initData() {
        binding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.requestLogin();
            }
        });
    }

    @Override
    public void initViewObservable() {
        String account = SPUtils.getInstance().getString("account");
        String password = SPUtils.getInstance().getString("password");
        viewModel.userName.set(account);
        viewModel.userPassword.set(password);

        viewModel.getUserBeanObservable().observe(this, new Observer<Result<UserBean>>() {
            @Override
            public void onChanged(Result<UserBean> userBeanResult) {
                if(userBeanResult.getReturnCode()==200){
                    ToastUtils.showLong("登录成功");
                    App.getInstance().saveUserBean(userBeanResult.data);
                    SPUtils.getInstance().put("account",viewModel.userName.get());
                    SPUtils.getInstance().put("password",viewModel.userPassword.get());
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    ToastUtils.showLong("登录失败");
                }
            }
        });
    }


}