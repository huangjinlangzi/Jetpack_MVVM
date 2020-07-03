package com.example.wushengqing.app;

import androidx.lifecycle.MutableLiveData;

import com.example.wushengqing.bus.event.SingleLiveEvent;
import com.example.wushengqing.data.net.callback.UnPeekLiveData;

public class ConstantCode {
    public static MutableLiveData mutableLiveData = new MutableLiveData();
    public static UnPeekLiveData unPeekLiveData = new UnPeekLiveData();
    public static SingleLiveEvent singleLiveEvent = new SingleLiveEvent();
}
