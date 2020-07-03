package com.example.wushengqing.data.net.callback;

public interface MyStringCallBack {
    void onSuccess(String response);
    void onFail(String message);
    void onError(String errorMessage);
}
