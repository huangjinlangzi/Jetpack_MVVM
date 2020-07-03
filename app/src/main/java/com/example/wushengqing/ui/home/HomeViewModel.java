package com.example.wushengqing.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.wushengqing.base.BaseViewModel;


public class HomeViewModel extends BaseViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Integer> counter;

    public MutableLiveData liveData;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }
    public HomeViewModel(@NonNull Application application,int counterRestore) {
        super(application);
        mText = new MutableLiveData<>();
        liveData = new MutableLiveData();
        counter=new MutableLiveData<>();
        counter.setValue(counterRestore);
    }


    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Integer> getCounter(){
        return counter;
    }

    public void plusOne(){
        int count= counter.getValue()==null?0:counter.getValue();
        count++;
        counter.setValue(count);
    }

    public void clear(){
        counter.setValue(0);
    }

    public void error(){
        liveData.setValue(null);
    }
}